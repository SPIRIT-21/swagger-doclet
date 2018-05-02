package com.spirit21.handler.parameter;

import com.spirit21.common.handler.parameter.ParameterAnnotationHandler;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.parameters.PathParameter;

public class PathParameterHandler extends AbstractParameterHandler<PathParameter> implements ParameterAnnotationHandler {
	
	private final String name;
	
	public PathParameterHandler(String name) {
		this.name = name;
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

	@Override
	public String getName() {
		return name;
	}

}
