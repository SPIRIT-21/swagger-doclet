package com.spirit21.common.handler.parameter;

import com.spirit21.common.Consts;
import com.spirit21.common.handler.property.PropertyFactory;
import com.spirit21.common.helper.CommonHelper;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.parameters.AbstractSerializableParameter;

/**
 * Abstract class from which every HTTP parameter handler will inherit.
 * Only the body parameter handler will not inherit from this class.
 * 
 * @author mweidmann
 *
 * @param <T> The HTTP swagger parameter class except of the body parameter.
 */
public abstract class AbstractParameterHandler<T extends AbstractSerializableParameter<T>> extends AbstractHandler {
	
	private final String httpParameterName;
	private final String defaultValueAnnotationName;
	private final String defaultValueAnnotationProperty;
	
	public AbstractParameterHandler(MethodDoc methodDoc, Parameter javaDocParameter, String httpParameterName, String defaultValueAnnotationName, String defaultValueAnnotationProperty) {
		super(methodDoc, javaDocParameter);
		this.httpParameterName = httpParameterName;
		this.defaultValueAnnotationName = defaultValueAnnotationName;
		this.defaultValueAnnotationProperty = defaultValueAnnotationProperty;
	}

	/**
	 * Sets the name, default value, description and the property to a passed Swagger parameter object.
	 * 
	 * @param swaggerParameter The swagger parameter in which the data will be set.
	 */
	protected void setDataToSwaggerParameter(AbstractSerializableParameter<T> swaggerParameter) {
		// Set the parameter name.
		AnnotationValue aValue = CommonHelper.getAnnotationValue(javaDocParameter, getHttpParameterType(), Consts.ANNOTATION_PROPERTY_NAME_VALUE);
		String value = (String) CommonHelper.getAnnotationValueObject(aValue);
		swaggerParameter.setName(value);
		
		// Set the default value.
		String defaultValue = getDefaultValue();
		if (defaultValue != null) {
			swaggerParameter.setDefaultValue(defaultValue);
		}

		// Set the description.
		swaggerParameter.setDescription(getDescriptionForJavaDocParameter());

		// Set the property.
		swaggerParameter.setProperty(PropertyFactory.createSwaggerProperty(javaDocParameter.type()));
	}
	
	/**
	 * Gets the default value of a parameterDoc.
	 * 
	 * @param parameterDoc The parameterDoc from where the default value is retrieved.
	 * @return A string which contains the default value for a parameter or null.
	 */
	protected String getDefaultValue() {
		AnnotationValue aValue = CommonHelper.getAnnotationValue(javaDocParameter, defaultValueAnnotationName, defaultValueAnnotationProperty);
		return (String) CommonHelper.getAnnotationValueObject(aValue);
	}
	
	@Override
	public String getHttpParameterType() {
		return httpParameterName;
	}
}
