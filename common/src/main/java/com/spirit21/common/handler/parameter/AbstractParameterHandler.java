package com.spirit21.common.handler.parameter;

import com.spirit21.common.Consts;
import com.spirit21.common.handler.property.PropertyFactory;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.common.parser.AbstractParser;
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
public abstract class AbstractParameterHandler<T extends AbstractSerializableParameter<T>> implements IParameterHandler {
	
	private final String httpParameterName;
	private final String defaultValueAnnotationName;
	private final String defaultValueAnnotationValue;
	
	public AbstractParameterHandler(String httpParameterName, String defaultValueAnnotationName, String defaultValueAnnotationValue) {
		this.httpParameterName = httpParameterName;
		this.defaultValueAnnotationName = defaultValueAnnotationName;
		this.defaultValueAnnotationValue = defaultValueAnnotationValue;
	}

	/**
	 * Sets the name, default value, description and the property to a passed Swagger parameter object.
	 * 
	 * @param swaggerParameter The swagger parameter in which the data will be set.
	 * @param parameterDoc The parameterDoc from where the data comes from.
	 * @param methodDoc The methodDoc which contains the passed parameterDoc.
	 */
	protected void setDataToParameter(AbstractSerializableParameter<T> swaggerParameter, Parameter parameterDoc, MethodDoc methodDoc) {
		// Set the parameter name.
		AnnotationValue aValue = CommonHelper.getAnnotationValue(parameterDoc, getHttpParameterType(), Consts.VALUE);
		String value = (String) CommonHelper.getAnnotationValueObject(aValue);
		swaggerParameter.setName(value);
		
		// Set the default value.
		// TODO: change default value behaviour if changed to empty string.
		String defaultValue = getDefaultValue(parameterDoc);
		if (defaultValue != null) {
			swaggerParameter.setDefaultValue(defaultValue);
		}

		// Set the description.
		swaggerParameter.setDescription(IParameterHandler.getDescriptionForParameters(methodDoc, parameterDoc));

		// Set the property.
		swaggerParameter.setProperty(PropertyFactory.createProperty(parameterDoc.type(), AbstractParser.definitionClassDocs, AbstractParser.classDocCache));
	}
	
	/**
	 * Gets the default value of a parameterDoc.
	 * 
	 * @param parameterDoc The parameterDoc from where the default value is retrieved.
	 * @return A string which contains the default value for a parameter or null.
	 */
	// TODO: change javadoc if the change to an empty string of getAnnotationValueObject will be done.
	protected String getDefaultValue(Parameter parameterDoc) {
		AnnotationValue aValue = CommonHelper.getAnnotationValue(parameterDoc, defaultValueAnnotationName, defaultValueAnnotationValue);
		return (String) CommonHelper.getAnnotationValueObject(aValue);
	}
	
	@Override
	public String getHttpParameterType() {
		return httpParameterName;
	}
}
