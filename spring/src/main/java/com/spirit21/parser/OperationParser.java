package com.spirit21.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.spirit21.common.Consts;
import com.spirit21.common.exception.OperationParserException;
import com.spirit21.common.handler.javadoc.ResponseTagHandler;
import com.spirit21.handler.parameter.ParameterFactory;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.AnnotationValue;
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
	 * This method creates a list of operations for one method
	 */
	protected Map<String, Operation> createOperations(MethodDoc methodDoc, String pathName) {
		Map<String, Operation> operations = new HashMap<>();

		for (String httpMethod : ParserHelper.getHttpMethods(methodDoc)) {
			Operation operation = createOperation(methodDoc, pathName, httpMethod);
			operations.put(httpMethod, operation);
		}
		return operations;
	}

	/**
	 * This method creates an operation and calls other methods to set the operation
	 * properties
	 */
	private Operation createOperation(MethodDoc methodDoc, String pathName, String httpMethod) {
		Operation operation = new Operation();

		operation.setOperationId(httpMethod + pathName);
		setTags(operation, pathName);
		setMediaType(operation, methodDoc);
		setDescription(operation, methodDoc);

		try {
			setResponses(operation, methodDoc);
		} catch (OperationParserException e) {
			log.log(Level.SEVERE, "Error while creating operation for a path!", e);
		}
		setParameters(operation, methodDoc);
		setDeprecated(operation, methodDoc);

		return operation;
	}

	/**
	 * This method sets the tag(s) for the operation
	 */
	private void setTags(Operation operation, String value) {
		List<String> tags = new ArrayList<>();

		Matcher matcher = Parser.pattern.matcher(value);
		if (matcher.matches()) {
			tags.add(matcher.group(1));
		}
		operation.setTags(tags);
	}

	/**
	 * This method sets the MIME media type of the operation
	 */
	private void setMediaType(Operation operation, MethodDoc methodDoc) {
		String annotation = null;

		if (ParserHelper.hasRequestMappingAnnotation(methodDoc)) {
			annotation = RequestMapping.class.getName();
		} else if (ParserHelper.getHttpMappingAnnotation(methodDoc) != null) {
			annotation = ParserHelper.getHttpMappingAnnotation(methodDoc);
		}

		AnnotationValue aProduces = ParserHelper.getAnnotationValue(methodDoc, annotation, com.spirit21.Consts.PRODUCES);
		String[] produces = (String[]) ParserHelper.getAnnotationValueObject(aProduces);

		AnnotationValue aConsumes = ParserHelper.getAnnotationValue(methodDoc, annotation, com.spirit21.Consts.CONSUMES);
		String[] consumes = (String[]) ParserHelper.getAnnotationValueObject(aConsumes);

		if (produces != null) Arrays.asList(produces).forEach(operation::addProduces);
		if (consumes != null) Arrays.asList(consumes).forEach(operation::addConsumes);
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
	 * This method sets the responses for an operation and throw an exception if
	 * there is no response documented
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
	 * This method sets the response properties of the response object. E.g. the
	 * response message
	 */
	private void setResponseProperties(Tag tag, Response response) {
		Arrays.asList(ResponseTagHandler.values()).stream()
			.filter(rth -> rth.getName().equals(tag.name()))
			.forEach(rth -> rth.setResponseData(response, tag, Parser.definitionClassDocs, Parser.classDocCache));
	}

	/**
	 * This method analyzes the paramteres of a methodDoc, puts them in a list and
	 * adds it to the operation
	 */
	private void setParameters(Operation operation, MethodDoc methodDoc) {
		List<Parameter> parameters = Arrays.asList(methodDoc.parameters()).stream()
				.filter(param -> !ParserHelper.hasAnnotation(param, PathVariable.class.getName()))
				.map(param -> parameterFactory.getParameter(methodDoc, param))
				.collect(Collectors.toList());

		checkBodyParameters(parameters, methodDoc);

		operation.setParameters(parameters);
	}

	/**
	 * This method checks the amount of the body parameters and if there is more
	 * than one body parameter, it picks one and removes the other body parameters
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
	private void setDeprecated(Operation operation, MethodDoc methodDoc) {
		if (ParserHelper.hasAnnotation(methodDoc.containingClass(), Deprecated.class.getName()) 
				|| ParserHelper.hasAnnotation(methodDoc, Deprecated.class.getName())) {
			operation.setDeprecated(true);
		}
	}
}