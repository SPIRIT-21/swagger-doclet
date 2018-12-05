package com.spirit21.jaxrs.handler.parameter;

import javax.ws.rs.DefaultValue;

import com.spirit21.common.Consts;
import com.spirit21.common.handler.parameter.AbstractParameterHandler;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import v2.io.swagger.models.parameters.HeaderParameter;

public class HeaderParameterHandler extends AbstractParameterHandler<HeaderParameter> {
	
	public HeaderParameterHandler(String name) {
		super(name, DefaultValue.class.getName(), Consts.VALUE);
	}

	/** 
	 * This method creates a new header parameter sets the data and returns it
	 */
	@Override
	public HeaderParameter createNewParameter(Parameter parameter, MethodDoc methodDoc) {
		HeaderParameter headerParameter = new HeaderParameter();
		handleParameter(headerParameter, parameter, methodDoc);
		return headerParameter;
	}
}
