package com.spirit21.spring.handler.parameter;

import com.spirit21.common.handler.parameter.AbstractParameterHandler;
import com.spirit21.spring.Consts;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.QueryParameter;

/**
 * Query parameter handler for Spring Boot's query parameters.
 * 
 * @author mweidmann
 */
public class QueryParameterHandler extends AbstractParameterHandler<QueryParameter> {
	
	public QueryParameterHandler(String httpParameterName) {
		super(httpParameterName, httpParameterName, Consts.DEFAULT_VALUE);
	}
	
	@Override
	public Parameter createNewSwaggerParameter(MethodDoc methodDoc, com.sun.javadoc.Parameter javaDocParameter) {
		QueryParameter queryParameter = new QueryParameter();
		setDataToSwaggerParameter(queryParameter, methodDoc, javaDocParameter);
		return queryParameter;
	}
}
