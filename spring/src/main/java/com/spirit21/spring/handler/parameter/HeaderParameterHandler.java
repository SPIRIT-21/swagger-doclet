package com.spirit21.spring.handler.parameter;

import com.spirit21.common.handler.parameter.AbstractParameterHandler;
import com.spirit21.spring.Consts;
import com.sun.javadoc.MethodDoc;

import v2.io.swagger.models.parameters.HeaderParameter;
import v2.io.swagger.models.parameters.Parameter;

public class HeaderParameterHandler extends AbstractParameterHandler<HeaderParameter> {
	
	public HeaderParameterHandler(String name) {
		super(name, name, Consts.DEFAULT_VALUE);
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
}
