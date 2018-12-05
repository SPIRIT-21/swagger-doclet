package com.spirit21.common.handler.parameter;

import com.spirit21.common.handler.property.PropertyFactory;
import com.spirit21.common.parser.AbstractParser;
import com.sun.javadoc.MethodDoc;

import v2.io.swagger.models.parameters.BodyParameter;
import v2.io.swagger.models.parameters.Parameter;
import v2.io.swagger.models.properties.Property;
import v2.io.swagger.models.properties.PropertyBuilder;

public abstract class AbstractBodyParameterHandler implements ParameterAnnotationHandler {
	
	/**
	 * This method creates a new body parameter sets the data and returns it
	 */
	@Override
	public Parameter createNewParameter(com.sun.javadoc.Parameter parameter, MethodDoc methodDoc) {
		BodyParameter bodyParameter = new BodyParameter();

		// set name
		bodyParameter.setName(parameter.name());

		// set description
		bodyParameter.setDescription(ParameterAnnotationHandler.getDescriptionForParameters(methodDoc, parameter));

		// set schema
		Property property = PropertyFactory.createProperty(parameter.type(), AbstractParser.definitionClassDocs, AbstractParser.classDocCache);
		bodyParameter.setSchema(PropertyBuilder.toModel(property));

		return bodyParameter;
	}
}
