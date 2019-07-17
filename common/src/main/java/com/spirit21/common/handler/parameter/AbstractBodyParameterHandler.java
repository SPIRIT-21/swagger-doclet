package com.spirit21.common.handler.parameter;

import com.spirit21.common.handler.property.PropertyFactory;
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
public abstract class AbstractBodyParameterHandler extends AbstractHandler {
	
	public AbstractBodyParameterHandler(MethodDoc methodDoc, com.sun.javadoc.Parameter javaDocParameter) {
		super(methodDoc, javaDocParameter);
	}

	@Override
	public Parameter createNewSwaggerParameter() {
		// Create swagger body parameter.
		BodyParameter bodyParameter = new BodyParameter();

		// Set the name of the parameter.
		bodyParameter.setName(javaDocParameter.name());

		// Set the description of the parameter.
		bodyParameter.setDescription(getDescriptionForJavaDocParameter());

		// Set the schema of the parameter.
		Property property = PropertyFactory.createSwaggerProperty(javaDocParameter.type());
		bodyParameter.setSchema(PropertyBuilder.toModel(property));

		return bodyParameter;
	}
}
