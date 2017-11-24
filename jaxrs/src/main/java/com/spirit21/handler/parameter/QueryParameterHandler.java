package com.spirit21.handler.parameter;

import com.spirit21.Consts;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.parameters.QueryParameter;

public class QueryParameterHandler implements ParameterAnnotationHandler {

	private final String name;

	public QueryParameterHandler(String name) {
		this.name = name;
	}
	
	/** 
	 * This method creates a new QueryParameter sets the data and returns it
	 */
	@Override
	public QueryParameter createNewParameter(AnnotationDesc annotation, Parameter parameter, MethodDoc methodDoc) {
		QueryParameter qp = new QueryParameter();

		// set parameterName
		qp.setName(annotation.elementValues()[0].value().toString().replaceAll(Consts.QUOTATION_MARK,
				Consts.EMPTY_STRING));
		
		// set default value
		String defaultValue = ParameterAnnotationHandler.getDefaultValue(parameter);
		if (defaultValue != null) {
			qp.setDefaultValue(defaultValue);
		}
		
		// set description
		qp.setDescription(ParameterAnnotationHandler.getDescriptionForParameters(methodDoc, parameter));
		
		// set property
		String[] typeAndFormat = ParserHelper.checkTypeAndFormat(parameter.type());
		qp.setProperty(ParserHelper.createProperty(typeAndFormat, parameter.type()));
		
		return qp;
	}

	public String getName() {
		return name;
	}
}
