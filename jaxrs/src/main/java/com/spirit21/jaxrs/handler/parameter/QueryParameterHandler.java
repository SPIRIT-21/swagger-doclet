package com.spirit21.jaxrs.handler.parameter;

import com.spirit21.common.handler.parameter.ParameterAnnotationHandler;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import v2.io.swagger.models.parameters.QueryParameter;

public class QueryParameterHandler extends AbstractParameterHandler<QueryParameter> implements ParameterAnnotationHandler {

	private final String name;

	public QueryParameterHandler(String name) {
		this.name = name;
	}
	
	/** 
	 * This method creates a new query parameter sets the data and returns it
	 */
	@Override
	public QueryParameter createNewParameter(Parameter parameter, MethodDoc methodDoc) {
		QueryParameter queryParameter = new QueryParameter();
		handleParameter(queryParameter, parameter, methodDoc);
		return queryParameter;
	}
	
	@Override
	public String getName() {
		return name;
	}
}
