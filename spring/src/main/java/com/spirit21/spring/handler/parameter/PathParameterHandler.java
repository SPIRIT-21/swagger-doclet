package com.spirit21.spring.handler.parameter;

import com.spirit21.common.handler.parameter.AbstractParameterHandler;
import com.spirit21.spring.Consts;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.parameters.PathParameter;

/**
 * Path parameter handler for Spring Boot's path parameters.
 * 
 * @author mweidmann
 */
public class PathParameterHandler extends AbstractParameterHandler<PathParameter> {
	
	public PathParameterHandler(String httpParameterName) {
		super(httpParameterName, httpParameterName, Consts.ANNOTATION_PROPERTY_DEFAULT_VALUE);
	}
	
	@Override
	public PathParameter createNewSwaggerParameter(MethodDoc methodDoc, com.sun.javadoc.Parameter parameter) {
		PathParameter pathParameter = new PathParameter();
		setDataToSwaggerParameter(pathParameter, methodDoc, parameter);
		return pathParameter;
	}
}
