package com.spirit21.common.handler.parameter;

import com.spirit21.common.Consts;
import com.spirit21.common.handler.property.PropertyFactory;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.common.parser.AbstractParser;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import v2.io.swagger.models.parameters.AbstractSerializableParameter;

public abstract class AbstractParameterHandler<T extends AbstractSerializableParameter<T>> implements ParameterAnnotationHandler {
	
	protected final String name;
	private final String annotation;
	private final String specificAnnotation;
	
	public AbstractParameterHandler(String name, String annotation, String specificAnnotation) {
		this.name = name;
		this.annotation = annotation;
		this.specificAnnotation = specificAnnotation;
	}

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
		asp.setProperty(PropertyFactory.createProperty(parameter.type(), AbstractParser.definitionClassDocs, AbstractParser.classDocCache));
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * This method gets the default value of a parameter
	 */
	protected String getDefaultValue(Parameter parameter) {
		AnnotationValue aValue = CommonHelper.getAnnotationValue(parameter, annotation, specificAnnotation);
		return (String) CommonHelper.getAnnotationValueObject(aValue);
	}
}
