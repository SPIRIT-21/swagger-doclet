package com.spirit21.handler.parameter;

import com.spirit21.handler.property.PropertyFactory;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.PropertyBuilder;

public class BodyParameterHandler implements ParameterAnnotationHandler {
	
	/**
	 * This method creates a new BodyParameter sets the data and returns it
	 */
	@Override
	public BodyParameter createNewParameter(Parameter parameter, MethodDoc methodDoc) {
		BodyParameter bodyParameter = new BodyParameter();

		// set name
		bodyParameter.setName(parameter.name());
		
		// set Description
		bodyParameter.setDescription(ParameterAnnotationHandler.getDescriptionForParameters(methodDoc, parameter));
		
		// set schema
		Property property = PropertyFactory.createProperty(parameter.type());
		bodyParameter.setSchema(PropertyBuilder.toModel(property));
		
		return bodyParameter;
	}
	
	@Override
	public String getName() {
		return null;
	}
}