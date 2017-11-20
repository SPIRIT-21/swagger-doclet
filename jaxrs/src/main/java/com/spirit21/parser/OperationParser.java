package com.spirit21.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.spirit21.Consts;
import com.spirit21.exception.OperationParserException;
import com.spirit21.handler.annotation.MediaTypeHandler;
import com.spirit21.handler.javadoc.ResponseTagHandler;
import com.spirit21.handler.parameter.BodyParameterHandler;
import com.spirit21.handler.parameter.QueryParameterHandler;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;

import io.swagger.models.Operation;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import lombok.extern.java.Log;

@Log
public class OperationParser {

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

	private void setOperationId(Operation operation, MethodDoc methodDoc) {
		operation.setOperationId(ParserHelper.getSimpleHttpMethod(methodDoc).toLowerCase()
				+ ParserHelper.getPath(methodDoc.containingClass()));
	}

	private void setTags(Swagger swagger, Operation operation, ClassDoc classDoc) {
		List<String> tags = new ArrayList<>();
		
		ClassDoc parentClassDoc = ParserHelper.getParentClassDoc(classDoc);
		String annotationValue = ParserHelper.getAnnotationValue(parentClassDoc, Path.class.getName());
		
		swagger.getTags().stream()
			.filter(t -> annotationValue.contains(t.getName()))
			.forEach(t -> tags.add(t.getName()));
		
		operation.setTags(tags);
	}

	private void setMediaType(Operation operation, MethodDoc methodDoc) {
		for (AnnotationDesc annotation : methodDoc.annotations()) {
			Arrays.asList(MediaTypeHandler.values()).stream()
				.filter(mth -> annotation.annotationType().toString().equals(mth.getName()))
				.forEach(mth -> mth.setValue(operation, annotation.elementValues()));
		}
	}

	private void setDescription(Operation operation, MethodDoc methodDoc) {
		StringBuilder description = new StringBuilder();
		for (Tag tag : methodDoc.inlineTags()) {
			description.append(tag.text());
		}
		operation.setDescription(description.toString());
	}

	private void setResponses(Operation operation, MethodDoc methodDoc) throws OperationParserException {
		Map<String, Response> responses = new HashMap<>();
		String currentKey = "";
		for (Tag tag : methodDoc.tags()) {
			if (tag.name().equals(Consts.RESPONSE_CODE)) {
				currentKey = tag.text();
				responses.put(tag.text(), new Response());
				continue;
			}
			for (ResponseTagHandler rth : ResponseTagHandler.values()) {
				if (tag.name().equals(rth.getName())) {
					rth.setResponseData(responses.get(currentKey), tag);
					break;
				}
			}
		}
		if (responses.size() == 0) {
			throw new OperationParserException("In your documentation of the method '" + methodDoc.name() + "' in '"
					+ methodDoc.containingClass().qualifiedName() + "' you have to document at least one response!");
		}
		operation.setResponses(responses);
	}

	private void setParameters(Operation operation, MethodDoc methodDoc) throws OperationParserException {
		List<Parameter> parameters = new ArrayList<>();
		QueryParameterHandler queryParameter = new QueryParameterHandler(QueryParam.class.getName());
		BodyParameterHandler bodyParameter = new BodyParameterHandler();
		int counter = 0;
		for (com.sun.javadoc.Parameter parameter : methodDoc.parameters()) {
			if (ParserHelper.hasAnnotation(parameter, QueryParam.class.getName())) {
				for (AnnotationDesc annotation : parameter.annotations()) {
					if (annotation.annotationType().toString().equals(queryParameter.getName())) {
						Parameter queryParam = queryParameter.createNewParameter(annotation, parameter, methodDoc);
						parameters.add(queryParam);
					}
				}
			} else {
				Parameter bodyParam = bodyParameter.createNewParameter(null, parameter, methodDoc);
				parameters.add(bodyParam);
				counter++;
			}
		}
		if (counter > 1) {
			throw new OperationParserException(
					"The method " + methodDoc.name() + " in " + methodDoc.containingClass().qualifiedName()
							+ " has more than one BodyParameter! A method cannot have more than one BodyParameter.");
		}
		operation.setParameters(parameters);
	}
}