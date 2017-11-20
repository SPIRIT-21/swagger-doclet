package com.spirit21.parser;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.spirit21.Consts;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;

import io.swagger.models.Operation;

public class OperationParser {

	protected Operation createOperation(MethodDoc methodDoc) {
		Operation operation = new Operation();
		setOperationId(operation, methodDoc);
		setTags(operation, methodDoc);
		setMediaType(operation, methodDoc);
		setDescription(operation, methodDoc);
		setResponses(operation, methodDoc);
		setParameters(operation, methodDoc);
		return operation;
	}

	private void setOperationId(Operation operation, MethodDoc methodDoc) {
		AnnotationDesc httpMethod = ParserHelper.getHttpMethod(methodDoc);
		if (httpMethod != null) {
			String s = httpMethod.annotationType().simpleTypeName().toLowerCase();
			operation.setOperationId(s + ParserHelper.getFullPath(methodDoc));
		} else {
			// EXCEPTION
		}
	}

	private void setTags(Operation operation, MethodDoc methodDoc) {
		List<String> tags = new ArrayList<>();
		if (ParserHelper.hasAnnotation(methodDoc.containingClass(), RequestMapping.class.getName())) {
			String path = ParserHelper.getPathOrValueOfAnnotation(methodDoc.containingClass(), RequestMapping.class.getName());
			tags.add(path);
		}
		operation.setTags(tags);
	}

	private void setMediaType(Operation operation, MethodDoc methodDoc) {
		String consumes = ParserHelper.getAnnotationValue(methodDoc, RequestMapping.class.getName(), Consts.CONSUMES);
		String produces = ParserHelper.getAnnotationValue(methodDoc, RequestMapping.class.getName(), Consts.PRODUCES);
		if (consumes != null) {
			operation.addConsumes(consumes);
		}
		if (produces != null) {
			operation.addProduces(produces);
		}
	}

	private void setDescription(Operation operation, MethodDoc methodDoc) {
		StringBuilder description = new StringBuilder();
		for (Tag tag : methodDoc.inlineTags()) {
			description.append(tag.text());
		}
		operation.setDescription(description.toString());
	}

	private void setResponses(Operation operation, MethodDoc methodDoc) {
		
	}

	private void setParameters(Operation operation, MethodDoc methodDoc) {

	}
}