package com.spirit21.common.handler.parameter;

import com.spirit21.common.handler.property.PropertyFactory;
import com.spirit21.common.parser.AbstractParser;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.PropertyBuilder;

/**
 * Abstract class from which every HTTP body parameter handler will inherit.
 * 
 * @author mweidmann
 */
public abstract class AbstractBodyParameterHandler implements IParameterHandler {
	
	@Override
	public Parameter createNewSwaggerParameter(com.sun.javadoc.Parameter parameter, MethodDoc methodDoc) {
		// Create swagger body parameter.
		BodyParameter bodyParameter = new BodyParameter();

		// Set the name of the parameter.
		bodyParameter.setName(parameter.name());

		// Set the description of the parameter.
		bodyParameter.setDescription(IParameterHandler.getDescriptionForParameters(methodDoc, parameter));

		// Set the schema of the parameter.
		Property property = PropertyFactory.createProperty(parameter.type(), AbstractParser.definitionClassDocs, AbstractParser.classDocCache);
		bodyParameter.setSchema(PropertyBuilder.toModel(property));

		return bodyParameter;
	}
}
