package com.spirit21.handler.parameter;

import com.spirit21.common.handler.parameter.ParameterAnnotationHandler;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.parameters.HeaderParameter;

public class HeaderParameterHandler extends AbstractParameterHandler<HeaderParameter> implements ParameterAnnotationHandler {
	
	private final String name;
	
	public HeaderParameterHandler(String name) {
		this.name = name;
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
	
	@Override
	public String getName() {
		return name;
	}
}