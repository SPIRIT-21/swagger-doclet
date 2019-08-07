package com.spirit21.spring.handler.parameter;

import com.spirit21.common.handler.parameter.AbstractParameterHandler;
import com.spirit21.spring.Consts;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.Parameter;

/**
 * Header parameter handler for Spring Boot's header parameters.
 * 
 * @author mweidmann
 */
public class HeaderParameterHandler extends AbstractParameterHandler<HeaderParameter> {
	
	public HeaderParameterHandler(String httpParameterName) {
		super(httpParameterName, httpParameterName, Consts.ANNOTATION_PROPERTY_DEFAULT_VALUE);
	}
	
	@Override
	public Parameter createNewSwaggerParameter(MethodDoc methodDoc, com.sun.javadoc.Parameter javaDocParameter) {
		HeaderParameter headerParameter = new HeaderParameter();
		setDataToSwaggerParameter(headerParameter, methodDoc, javaDocParameter);
		return headerParameter;
	}
}
