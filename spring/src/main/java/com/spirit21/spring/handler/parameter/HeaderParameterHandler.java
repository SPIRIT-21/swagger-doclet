package com.spirit21.spring.handler.parameter;

import com.spirit21.common.handler.parameter.ParameterAnnotationHandler;
import com.sun.javadoc.MethodDoc;

import v2.io.swagger.models.parameters.HeaderParameter;
import v2.io.swagger.models.parameters.Parameter;

public class HeaderParameterHandler extends AbstractParameterHandler<HeaderParameter> implements ParameterAnnotationHandler {
	
	private final String name;
	
	public HeaderParameterHandler(String name) {
		this.name = name;
	}

	/**
	 * This method creates a new header parameter, sets the data of it and returns it
	 */
	@Override
	public Parameter createNewParameter(com.sun.javadoc.Parameter parameter, MethodDoc methodDoc) {
		HeaderParameter headerParameter = new HeaderParameter();
		handleParameter(headerParameter, parameter, methodDoc);
		return headerParameter;
	}

	@Override
	public String getName() {
		return name;
	}
}
