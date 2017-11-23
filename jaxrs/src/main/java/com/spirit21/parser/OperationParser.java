package com.spirit21.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.ws.rs.Path;

import com.spirit21.Consts;
import com.spirit21.exception.OperationParserException;
import com.spirit21.handler.annotation.MIMEMediaTypeHandler;
import com.spirit21.handler.javadoc.ResponseTagHandler;
import com.spirit21.handler.parameter.ParameterFactory;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;

import io.swagger.models.Operation;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import lombok.extern.java.Log;

@Log
public class OperationParser {

	private ParameterFactory parameterFactory;

	// Initialize
	protected OperationParser() {
		parameterFactory = new ParameterFactory();
	}

	// This method creates an operation and calls other methods to set the operation properties
	protected Operation createOperation(Swagger swagger, ClassDoc classDoc, MethodDoc methodDoc) {
		Operation operation = new Operation();
		setOperationId(operation, methodDoc);
		setTags(swagger, operation, classDoc);
		setMediaType(operation, methodDoc);
		setDescription(operation, methodDoc);
		try {
			setResponses(operation, methodDoc);
			setParameters(operation, methodDoc);
		} catch (OperationParserException e) {
			log.log(Level.SEVERE, "Error while creating operation for a path!", e);
		}
		return operation;
	}

	// This method sets the operationID consisting of httpMethod (get, post..) and the path
	private void setOperationId(Operation operation, MethodDoc methodDoc) {
		operation.setOperationId(ParserHelper.getSimpleHttpMethod(methodDoc).toLowerCase()
				+ ParserHelper.getPath(methodDoc.containingClass()));
	}

	// This method sets the tag(s) for the operation
	private void setTags(Swagger swagger, Operation operation, ClassDoc classDoc) {
		List<String> tags = new ArrayList<>();

		// Get the top-level ClassDoc and its @Path annotation value
		ClassDoc parentClassDoc = ParserHelper.getParentClassDoc(classDoc);
		String annotationValue = ParserHelper.getAnnotationValue(parentClassDoc, Path.class.getName(), Consts.VALUE);

		// Compare tags and the top-level ClassDoc path and finally add tag to operation
		swagger.getTags().stream().filter(t -> annotationValue.contains(t.getName()))
				.forEach(t -> tags.add(t.getName()));
		operation.setTags(tags);
	}

	// This method sets the MIME media type for the operation
	private void setMediaType(Operation operation, MethodDoc methodDoc) {
		// Iterate over possible media type annotations and filter existing values
		Arrays.asList(MIMEMediaTypeHandler.values()).stream()
				.filter(mth -> ParserHelper.hasAnnotation(methodDoc, mth.getName()))
				.forEach(mth -> mth.setValue(operation, methodDoc));
	}

	// This method sets the description of the operation
	private void setDescription(Operation operation, MethodDoc methodDoc) {
		StringBuilder description = new StringBuilder();
		Arrays.asList(methodDoc.inlineTags()).forEach(t -> description.append(t.text()));
		operation.setDescription(description.toString());
	}

	// This method sets the responses for an operation
	private void setResponses(Operation operation, MethodDoc methodDoc) throws OperationParserException {
		Map<String, Response> responses = new HashMap<>();
		String currentKey = "";

		// Iterate through the javadoc tags of the method
		for (Tag tag : methodDoc.tags()) {
			// Safe the response code in currentKey and put this key with a new response inmap
			if (tag.name().equals(Consts.RESPONSE_CODE)) {
				currentKey = tag.text();
				responses.put(tag.text(), new Response());
				continue;
			}
			// Iterate trough the ResposeTagHandler
			for (ResponseTagHandler rth : ResponseTagHandler.values()) {
				// Check if one of these ResponseTagHandler values equals the tag, if so then then set value in response
				if (tag.name().equals(rth.getName())) {
					rth.setResponseData(responses.get(currentKey), tag);
					break;
				}
			}
		}
		// If someone forgot to document at least one response, the doclet throws an exception
		if (responses.size() == 0) {
			throw new OperationParserException("In your documentation of the method '" + methodDoc.name() + "' in '"
					+ methodDoc.containingClass().qualifiedName() + "' you have to document at least one response!");
		}
		operation.setResponses(responses);
	}

	// This method analyzes the parameters of a methodDoc, puts them in a list and adds it to the operation
	private void setParameters(Operation operation, MethodDoc methodDoc) {
		List<Parameter> parameters = new ArrayList<>();

		int counter = 0;
		for (com.sun.javadoc.Parameter parameter : methodDoc.parameters()) {
			Parameter param = parameterFactory.getParameter(methodDoc, parameter);
			if (param != null && !(param instanceof BodyParameter)) {
				parameters.add(param);
			} else if (param != null){
				if (counter < 1) {
					parameters.add(param);
				}
				counter++;
			}
		}
		if (counter > 1) {
			log.info("The method '" + methodDoc + "' in the resource '" + methodDoc.containingClass()
					+ "' has more than one bodyParameter. Only one is allowed.");
		}
		operation.setParameters(parameters);
	}
}