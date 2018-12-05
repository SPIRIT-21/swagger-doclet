package com.spirit21.jaxrs.handler.parameter;

import javax.ws.rs.DefaultValue;

import com.spirit21.common.Consts;
import com.spirit21.common.handler.parameter.AbstractParameterHandler;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import v2.io.swagger.models.parameters.FormParameter;

public class FormParameterHandler extends AbstractParameterHandler<FormParameter> {
	
	public FormParameterHandler(String name) {
		super(name, DefaultValue.class.getName(), Consts.VALUE);
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
}
