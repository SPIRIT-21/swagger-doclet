package com.spirit21.jaxrs.handler.parameter;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import com.spirit21.common.handler.parameter.IParameterHandler;
import com.spirit21.common.helper.CommonHelper;
import com.sun.javadoc.MethodDoc;

import v2.io.swagger.models.parameters.Parameter;

public class ParameterFactory {

	private BodyParameterHandler bph;
	private List<IParameterHandler> handlers;

	public ParameterFactory() {
		this.handlers = new ArrayList<>();
		this.handlers.add(new FormParameterHandler(FormParam.class.getName()));
		this.handlers.add(new HeaderParameterHandler(HeaderParam.class.getName()));
		this.handlers.add(new QueryParameterHandler(QueryParam.class.getName()));
		this.bph = new BodyParameterHandler();
	}
	
	/**
	 * This method gets creates the swagger parameter model out of the doclet parameter
	 */
	public Parameter getParameter(MethodDoc methodDoc, com.sun.javadoc.Parameter parameter) {
		Parameter param = null;

		for (IParameterHandler pah : handlers) {
			if (CommonHelper.hasAnnotation(parameter, pah.getHttpParameterType())
					&& !CommonHelper.hasAnnotation(parameter, Context.class.getName())) {
				return pah.createNewSwaggerParameter(parameter, methodDoc);
			}
		}

		if (CommonHelper.hasAnnotation(parameter, Context.class.getName())) {
			return param;
		} else {
			return bph.createNewSwaggerParameter(parameter, methodDoc);
		}
	}
}