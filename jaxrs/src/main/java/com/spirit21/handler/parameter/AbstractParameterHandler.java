package com.spirit21.handler.parameter;

import javax.ws.rs.DefaultValue;

import com.spirit21.Consts;
import com.spirit21.handler.property.PropertyFactory;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.parameters.AbstractSerializableParameter;

public abstract class AbstractParameterHandler<T extends AbstractSerializableParameter<T>> {

	/**
	 * This method handles the parameters and sets the data of it
	 */
	protected void handleParameter(AbstractSerializableParameter<T> asp, Parameter parameter, MethodDoc methodDoc) {
		// set parameter name
		AnnotationValue aValue = ParserHelper.getAnnotationValue(parameter, getName(), Consts.VALUE);
		String value = (String) ParserHelper.getAnnotationValueObject(aValue);
		asp.setName(value);

		// set default value
		String defaultValue = getDefaultValue(parameter);
		if (defaultValue != null) {
			asp.setDefaultValue(defaultValue);
		}

		// set description
		asp.setDescription(ParameterAnnotationHandler.getDescriptionForParameters(methodDoc, parameter));

		// set property
		asp.setProperty(PropertyFactory.createProperty(parameter.type()));
	}
	
	/** 
	 * This method gets the DefaultValue of the Parameter
	 */
	protected String getDefaultValue(Parameter parameter) {
		AnnotationValue aValue = ParserHelper.getAnnotationValue(parameter, DefaultValue.class.getName(), Consts.VALUE);
		return (String) ParserHelper.getAnnotationValueObject(aValue);
	}
	
	protected abstract String getName();
}