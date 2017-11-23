package com.spirit21.handler.parameter;

import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.PropertyBuilder;

public class BodyParameterHandler implements ParameterAnnotationHandler {
	
	// This method creates a new BodyParameter sets the data and returns it
	@Override
	public BodyParameter createNewParameter(Parameter parameter, MethodDoc methodDoc) {
		BodyParameter bp = new BodyParameter();

		// set name
		bp.setName(parameter.name());
		
		// set Description
		bp.setDescription(ParameterAnnotationHandler.getDescriptionForParameters(methodDoc, parameter));
		
		// set schema
		String[] typeAndFormat = ParserHelper.checkTypeAndFormat(parameter.type());
		Property property = ParserHelper.createProperty(typeAndFormat, parameter.type());
		bp.setSchema(PropertyBuilder.toModel(property));
		
		return bp;
	}

}
