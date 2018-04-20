package com.spirit21.handler.parameter;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.parameters.FormParameter;

public class FormParameterHandler extends AbstractParameterHandler<FormParameter> implements ParameterAnnotationHandler {
	
	private final String name;
	
	public FormParameterHandler(String name) {
		this.name = name;
	}
	
	/** 
	 * This method creates a new FormParameter sets the data and returns it
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