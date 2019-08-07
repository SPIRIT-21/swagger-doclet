package com.spirit21.common.handler.parameter;

import com.spirit21.common.CommonConsts;
import com.spirit21.common.handler.property.PropertyFactory;
import com.spirit21.common.helper.CommonHelper;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.MethodDoc;

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
	
	public AbstractParameterHandler(String httpParameterName, String defaultValueAnnotationName, String defaultValueAnnotationProperty) {
		this.httpParameterName = httpParameterName;
		this.defaultValueAnnotationName = defaultValueAnnotationName;
		this.defaultValueAnnotationProperty = defaultValueAnnotationProperty;
	}

	/**
	 * Sets the name, default value, description and the property to a passed Swagger parameter object.
	 * 
	 * @param swaggerParameter The swagger parameter in which the data will be set.
	 * @param methodDoc The MethodDoc which contains the parameter.
	 * @param javaDocParameter The JavaDoc parameter which should be converted to a swagger parameter.
	 */
	protected void setDataToSwaggerParameter(AbstractSerializableParameter<T> swaggerParameter, MethodDoc methodDoc, com.sun.javadoc.Parameter javaDocParameter) {
		// Set the parameter name.
		AnnotationValue annotationValue = CommonHelper.getAnnotationValue(javaDocParameter, getHttpParameterType(), CommonConsts.ANNOTATION_PROPERTY_NAME_VALUE);
		String parameterName = (String) CommonHelper.getAnnotationValueObject(annotationValue);
		swaggerParameter.setName(parameterName);
		
		// Set the default value.
		String defaultValue = getDefaultValue(javaDocParameter);
		if (defaultValue != null) {
			swaggerParameter.setDefaultValue(defaultValue);
		}

		// Set the description.
		swaggerParameter.setDescription(getDescriptionForJavaDocParameter(methodDoc, javaDocParameter));

		// Set the property.
		swaggerParameter.setProperty(PropertyFactory.createSwaggerProperty(javaDocParameter.type()));
	}
	
	/**
	 * Gets the default value of a parameterDoc.
	 * 
	 * @param javaDocParameter The parameterDoc from where the default value is retrieved.
	 * @return A string which contains the default value for a parameter or null.
	 */
	protected String getDefaultValue(com.sun.javadoc.Parameter javaDocParameter) {
		AnnotationValue annotationValue = CommonHelper.getAnnotationValue(javaDocParameter, defaultValueAnnotationName, defaultValueAnnotationProperty);
		return (String) CommonHelper.getAnnotationValueObject(annotationValue);
	}
	
	@Override
	public String getHttpParameterType() {
		return httpParameterName;
	}
}
