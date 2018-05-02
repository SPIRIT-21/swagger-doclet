package com.spirit21.handler.parameter;

import com.spirit21.common.handler.parameter.ParameterAnnotationHandler;
import com.spirit21.common.handler.property.PropertyFactory;
import com.spirit21.parser.Parser;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.PropertyBuilder;

public class BodyParameterHandler implements ParameterAnnotationHandler {
	
	private final String name;
	
	public BodyParameterHandler(String name) {
		this.name = name;
	}
	
	/**
	 * This method creates a new body parameter, sets the data and returns it
	 */
	@Override
	public Parameter createNewParameter(com.sun.javadoc.Parameter parameter, MethodDoc methodDoc) {
		BodyParameter bodyParameter = new BodyParameter();
		
		// set name
		bodyParameter.setName(parameter.name());
		
		// set description
		bodyParameter.setDescription(ParameterAnnotationHandler.getDescriptionForParameters(methodDoc, parameter));
		
		// set schema
		Property property = PropertyFactory.createProperty(parameter.type(), Parser.definitionClassDocs, Parser.classDocCache);
		bodyParameter.setSchema(PropertyBuilder.toModel(property));
		
		return bodyParameter;
	}

	@Override
	public String getName() {
		return name;
	}
}