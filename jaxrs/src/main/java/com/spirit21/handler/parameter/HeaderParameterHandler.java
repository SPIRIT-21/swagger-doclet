package com.spirit21.handler.parameter;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.parameters.HeaderParameter;

public class HeaderParameterHandler extends AbstractParameterHandler<HeaderParameter> implements ParameterAnnotationHandler {
	
	private final String name;
	
	public HeaderParameterHandler(String name) {
		this.name = name;
	}
	
	/** 
	 * This method creates a new Header sets the data and returns it
	 */
	@Override
	public HeaderParameter createNewParameter(Parameter parameter, MethodDoc methodDoc) {
		HeaderParameter hp = new HeaderParameter();
		handleParameter(hp, parameter, methodDoc);
		return hp;
	}
	
	@Override
	public String getName() {
		return name;
	}
}