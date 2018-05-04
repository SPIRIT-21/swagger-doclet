package com.spirit21.spring.handler.parameter;

import com.spirit21.common.handler.parameter.ParameterAnnotationHandler;
import com.sun.javadoc.MethodDoc;

import v2.io.swagger.models.parameters.Parameter;
import v2.io.swagger.models.parameters.QueryParameter;

public class QueryParameterHandler extends AbstractParameterHandler<QueryParameter> implements ParameterAnnotationHandler {
	
	private final String name;

	public QueryParameterHandler(String name) {
		this.name = name;
	}
	
	/** 
	 * This method creates a new QueryParameter sets the data and returns it
	 */
	@Override
	public Parameter createNewParameter(com.sun.javadoc.Parameter parameter, MethodDoc methodDoc) {
		QueryParameter queryParameter = new QueryParameter();
		handleParameter(queryParameter, parameter, methodDoc);
		return queryParameter;
	}

	@Override
	public String getName() {
		return name;
	}
}
