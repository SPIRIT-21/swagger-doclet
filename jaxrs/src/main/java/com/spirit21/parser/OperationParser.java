package com.spirit21.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.ws.rs.Path;

import com.spirit21.common.Consts;
import com.spirit21.common.exception.OperationParserException;
import com.spirit21.common.handler.javadoc.ResponseTagHandler;
import com.spirit21.handler.annotation.MIMEMediaTypeHandler;
import com.spirit21.handler.parameter.ParameterFactory;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;

import io.swagger.models.Operation;
import io.swagger.models.Response;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import lombok.extern.java.Log;

@Log
public class OperationParser {

	private ParameterFactory parameterFactory;

	/**
	 * Initialize
	 */
	protected OperationParser() {
		parameterFactory = new ParameterFactory();
	}

	/**
	 * This method creates an operation and calls other methods to set the operation properties
	 */
	protected Operation createOperation(MethodDoc methodDoc) {
		Operation operation = new Operation();
		
		setOperationId(operation, methodDoc);
		setTags(operation, methodDoc);
		setMediaType(operation, methodDoc);
		setDescription(operation, methodDoc);
		
		try {
			setResponses(operation, methodDoc);
		} catch (OperationParserException e) {
			log.log(Level.SEVERE, "Error while creating operation for a path!", e);
		}
		
		setParameters(operation, methodDoc);
		setDeprecated(methodDoc, operation);
		
		return operation;
	}

	/**
	 * This method sets the operationID consisting of httpMethod (get, post..) and the path
	 */
	private void setOperationId(Operation operation, MethodDoc methodDoc) {
		operation.setOperationId(ParserHelper.getSimpleHttpMethod(methodDoc).toLowerCase()
				+ ParserHelper.getPath(methodDoc.containingClass()));
	}

	/**
	 * This method sets the tag(s) for the operation
	 */
	private void setTags(Operation operation, MethodDoc methodDoc) {
		List<String> tags = new ArrayList<>();

		ClassDoc parentClassDoc = ParserHelper.getParentClassDoc(methodDoc.containingClass());
		AnnotationValue aValue = ParserHelper.getAnnotationValue(parentClassDoc, Path.class.getName(), Consts.VALUE);
		String value = (String) ParserHelper.getAnnotationValueObject(aValue);
		
		Matcher matcher = Parser.pattern.matcher(value);
		
		if (matcher.matches()) {
			tags.add(matcher.group(1));
		}
		operation.setTags(tags);
	}

	/**
	 * This method sets the MIME media type for the operation
	 */
	private void setMediaType(Operation operation, MethodDoc methodDoc) {
		Arrays.asList(MIMEMediaTypeHandler.values()).stream()
			.filter(mth -> ParserHelper.hasAnnotation(methodDoc, mth.getMimeMediaType()))
			.forEach(mth -> mth.setValue(operation, methodDoc));
	}

	/**
	 * This method sets the description of the operation
	 */
	private void setDescription(Operation operation, MethodDoc methodDoc) {
		String description = Arrays.asList(methodDoc.inlineTags()).stream()
			.map(Tag::text)
			.collect(Collectors.joining());
		
		operation.setDescription(description);
	}

	/**
	 * This method sets the responses for an operation and throws an exception if the user forgot to document
	 * at least one response.
	 */
	private void setResponses(Operation operation, MethodDoc methodDoc) throws OperationParserException {
		Map<String, Response> responses = new HashMap<>();
		Response currentResponse = null;
		
		for (Tag tag : methodDoc.tags()) {
			if (tag.name().equals(Consts.RESPONSE_CODE)) {
				currentResponse = new Response();
				responses.put(tag.text(), currentResponse);
				continue;
			} else {
				setResponseProperties(tag, currentResponse);
			}
		}
		
		if (responses.size() == 0) {
			throw new OperationParserException("In your documentation of the method '" + methodDoc.name() + "' in '"
					+ methodDoc.containingClass().qualifiedName() + "' you have to document at least one response!");
		}
		operation.setResponses(responses);
	}
	
	/**
	 * This method sets the response properties of a response object. E.g. the response message.
	 */
	private void setResponseProperties(Tag tag, Response response) {
		Arrays.asList(ResponseTagHandler.values()).stream()
			.filter(rth -> rth.getName().equals(tag.name()))
			.forEach(rth -> rth.setResponseData(response, tag, Parser.definitionClassDocs, Parser.classDocCache));
	}

	/**
	 * This method analyzes the parameters of a methodDoc, puts them in a list and adds it to the operation
	 */
	private void setParameters(Operation operation, MethodDoc methodDoc) {
		List<Parameter> parameters = Arrays.asList(methodDoc.parameters()).stream()
						.map(param -> parameterFactory.getParameter(methodDoc, param))
						.collect(Collectors.toList());
		
		checkBodyParameters(parameters, methodDoc);
		
		operation.setParameters(parameters);
	}
	
	/**
	 * This method checks the amount of the body parameters and if there is more than one
	 * body parameter, it picks one and removes the other body parameters
	 */
	private void checkBodyParameters(List<Parameter> parameters, MethodDoc methodDoc) {
		List<Parameter> bodyParameters = parameters.stream()
			.filter(param -> param instanceof BodyParameter)
			.collect(Collectors.toList());
		
		if (bodyParameters.size() > 1) {
			bodyParameters.remove(0);
			parameters.removeAll(bodyParameters);
			log.info("The method '" + methodDoc + "' in the resource '" + methodDoc.containingClass()
						+ "' has more than one bodyParameter. Only one is allowed.");
		}
	}
	
	/**
	 * This method sets the operation to value, if the method or the class is annotated
	 * with the Deprecated annotation
	 */
	private void setDeprecated(MethodDoc methodDoc, Operation operation) {
		if (ParserHelper.hasAnnotation(methodDoc.containingClass(), Deprecated.class.getName()) 
				|| ParserHelper.hasAnnotation(methodDoc, Deprecated.class.getName())) {
			operation.setDeprecated(true);
		}
	}
}