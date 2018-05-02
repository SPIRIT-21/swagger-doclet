package com.spirit21.handler.parameter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.spirit21.common.handler.parameter.ParameterAnnotationHandler;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.parameters.Parameter;

public class ParameterFactory {
	
	private List<ParameterAnnotationHandler> handlers;
	
	public ParameterFactory() {
		handlers = new ArrayList<>();
		handlers.add(new BodyParameterHandler(RequestBody.class.getName()));
		handlers.add(new HeaderParameterHandler(RequestHeader.class.getName()));
		handlers.add(new QueryParameterHandler(RequestParam.class.getName()));
	}
	
	public Parameter getParameter(MethodDoc methodDoc, com.sun.javadoc.Parameter parameter) {
		for (ParameterAnnotationHandler pah : handlers) {
			if (ParserHelper.hasAnnotation(parameter, pah.getName())) {
				return pah.createNewParameter(parameter, methodDoc);
			}
		}
		return null;
	}
}
