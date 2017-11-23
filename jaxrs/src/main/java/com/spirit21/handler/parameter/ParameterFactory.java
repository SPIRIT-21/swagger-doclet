package com.spirit21.handler.parameter;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.parameters.Parameter;

public class ParameterFactory {

	private BodyParameterHandler bph;
	private List<ParameterAnnotationHandler> handlers;

	public ParameterFactory() {
		bph = new BodyParameterHandler();
		handlers = new ArrayList<>();
		handlers.add(new FormParameterHandler(FormParam.class.getName()));
		handlers.add(new HeaderParameterHandler(HeaderParam.class.getName()));
		handlers.add(new QueryParameterHandler(QueryParam.class.getName()));
	}

	public Parameter getParameter(MethodDoc methodDoc, com.sun.javadoc.Parameter parameter) {
		Parameter param = null;

		for (ParameterAnnotationHandler pah : handlers) {
			if (ParserHelper.hasAnnotation(parameter, pah.getName())
					&& !ParserHelper.hasAnnotation(parameter, Context.class.getName())) {
				param = pah.createNewParameter(parameter, methodDoc);
				return param;
			}
		}

		if (ParserHelper.hasAnnotation(parameter, Context.class.getName())) {
			return param;
		} else {
			param = bph.createNewParameter(parameter, methodDoc);
			return param;
		}
	}
}