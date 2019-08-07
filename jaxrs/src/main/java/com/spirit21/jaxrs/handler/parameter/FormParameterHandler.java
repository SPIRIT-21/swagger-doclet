package com.spirit21.jaxrs.handler.parameter;

import javax.ws.rs.DefaultValue;

import com.spirit21.common.CommonConsts;
import com.spirit21.common.handler.parameter.AbstractParameterHandler;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.parameters.FormParameter;

public class FormParameterHandler extends AbstractParameterHandler<FormParameter> {
	
	public FormParameterHandler(String name) {
		super(name, DefaultValue.class.getName(), CommonConsts.ANNOTATION_PROPERTY_NAME_VALUE);
	}
	
	/** 
	 * This method creates a new form parameter sets the data and returns it
	 */
	@Override
	public FormParameter createNewSwaggerParameter(Parameter parameter, MethodDoc methodDoc) {
		FormParameter formParameter = new FormParameter();
		setDataToParameter(formParameter, parameter, methodDoc);
		return formParameter;
	}
}
