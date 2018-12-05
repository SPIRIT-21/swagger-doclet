package com.spirit21.spring.handler.parameter;

import com.spirit21.common.handler.parameter.AbstractParameterHandler;
import com.spirit21.spring.Consts;
import com.sun.javadoc.MethodDoc;

import v2.io.swagger.models.parameters.PathParameter;

public class PathParameterHandler extends AbstractParameterHandler<PathParameter> {
	
	public PathParameterHandler(String name) {
		super(name, name, Consts.DEFAULT_VALUE);
	}
	
	/** 
	 * This method creates a new path parameter sets the data and returns it
	 */
	@Override
	public PathParameter createNewParameter(com.sun.javadoc.Parameter parameter, MethodDoc methodDoc) {
		PathParameter pathParameter = new PathParameter();
		handleParameter(pathParameter, parameter, methodDoc);
		return pathParameter;
	}
}
