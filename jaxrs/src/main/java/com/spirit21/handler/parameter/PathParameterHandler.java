package com.spirit21.handler.parameter;

import com.spirit21.Consts;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.parameters.PathParameter;

public class PathParameterHandler implements ParameterAnnotationHandler {

	private final String name;

	public PathParameterHandler(String name) {
		this.name = name;
	}
	
	// This method creates a new PathParameter sets the data and returns it
	@Override
	public PathParameter createNewParameter(Parameter parameter, MethodDoc methodDoc) {
		PathParameter pathParameter = new PathParameter();
		
		//set parameter name
		String parameterName = ParserHelper.getAnnotationValue(parameter, getName(), Consts.VALUE);
		pathParameter.setName(ParserHelper.replaceQuotationMarks(parameterName));

		// set defaultValue
		String defaultValue = ParameterAnnotationHandler.getDefaultValue(parameter);
		if (defaultValue != null) {
			pathParameter.setDefaultValue(defaultValue);
		}
		
		//set description
		pathParameter.setDescription(ParameterAnnotationHandler.getDescriptionForParameters(methodDoc, parameter));
		
		// set property
		String[] typeAndFormat = ParserHelper.checkTypeAndFormat(parameter.type());
		pathParameter.setProperty(ParserHelper.createProperty(typeAndFormat, parameter.type()));
		
		return pathParameter;
	}
	
	/**
	 * This method creates a new PathParameter of a FieldDoc sets the data and returns it
	 */
	public PathParameter createPathParameterFromField(FieldDoc fieldDoc) {
		PathParameter pathParameter = new PathParameter();
		
		// set name
		String annotationValue = ParserHelper.getAnnotationValue(fieldDoc, getName(), Consts.VALUE);
		pathParameter.setName(ParserHelper.replaceQuotationMarks(annotationValue));
		
		// set property
		String[] typeAndFormat = ParserHelper.checkTypeAndFormat(fieldDoc.type());
		pathParameter.setProperty(ParserHelper.createProperty(typeAndFormat, fieldDoc.type()));
		
		return pathParameter;
	}

	public String getName() {
		return name;
	}
}
