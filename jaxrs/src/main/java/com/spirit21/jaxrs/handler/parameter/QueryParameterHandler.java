package com.spirit21.jaxrs.handler.parameter;

import javax.ws.rs.DefaultValue;

import com.spirit21.common.Consts;
import com.spirit21.common.handler.parameter.AbstractParameterHandler;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import v2.io.swagger.models.parameters.QueryParameter;

public class QueryParameterHandler extends AbstractParameterHandler<QueryParameter> {

	public QueryParameterHandler(String name) {
		super(name, DefaultValue.class.getName(), Consts.VALUE);
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
}
