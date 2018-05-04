package com.spirit21.spring.handler.parameter;

import com.spirit21.common.Consts;
import com.spirit21.common.handler.parameter.ParameterAnnotationHandler;
import com.spirit21.common.handler.property.PropertyFactory;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.spring.parser.Parser;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import v2.io.swagger.models.parameters.AbstractSerializableParameter;

public abstract class AbstractParameterHandler<T extends AbstractSerializableParameter<T>> {
	
	/**
	 * This method handles the parameters and sets the data of it
	 */
	protected void handleParameter(AbstractSerializableParameter<T> asp, Parameter parameter, MethodDoc methodDoc) {
		// set parameter name
		AnnotationValue aValue = CommonHelper.getAnnotationValue(parameter, getName(), Consts.VALUE);
		String value = (String) CommonHelper.getAnnotationValueObject(aValue);
		asp.setName(value);
		
		// set default value
		String defaultValue = getDefaultValue(parameter);
		if (defaultValue != null) {
			asp.setDefaultValue(defaultValue);
		}
		
		// set description
		asp.setDescription(ParameterAnnotationHandler.getDescriptionForParameters(methodDoc, parameter));
		
		// set property
		asp.setProperty(PropertyFactory.createProperty(parameter.type(), Parser.definitionClassDocs, Parser.classDocCache));
	}
	
	/**
	 * This method gets the default value of a parameter
	 */
	protected String getDefaultValue(Parameter parameter) {
		AnnotationValue aValue = CommonHelper.getAnnotationValue(parameter, getName(), com.spirit21.spring.Consts.DEFAULT_VALUE);
		return (String) CommonHelper.getAnnotationValueObject(aValue);
	}
	
	protected abstract String getName();
}
