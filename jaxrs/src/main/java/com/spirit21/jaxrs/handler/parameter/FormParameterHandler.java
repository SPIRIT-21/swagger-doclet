package com.spirit21.jaxrs.handler.parameter;

import com.spirit21.common.handler.parameter.ParameterAnnotationHandler;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import v2.io.swagger.models.parameters.FormParameter;

public class FormParameterHandler extends AbstractParameterHandler<FormParameter> implements ParameterAnnotationHandler {
	
	private final String name;
	
	public FormParameterHandler(String name) {
		this.name = name;
	}
	
	/** 
	 * This method creates a new form parameter sets the data and returns it
	 */
	@Override
	public FormParameter createNewParameter(Parameter parameter, MethodDoc methodDoc) {
		FormParameter formParameter = new FormParameter();
		handleParameter(formParameter, parameter, methodDoc);
		return formParameter;
	}
	
	@Override
	public String getName() {
		return name;
	}
}