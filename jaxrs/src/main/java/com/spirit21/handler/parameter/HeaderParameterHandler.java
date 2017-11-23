package com.spirit21.handler.parameter;

import com.spirit21.Consts;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.parameters.HeaderParameter;

public class HeaderParameterHandler implements ParameterAnnotationHandler {
	
	private final String name;
	
	public HeaderParameterHandler(String name) {
		this.name = name;
	}

	@Override
	public HeaderParameter createNewParameter(Parameter parameter, MethodDoc methodDoc) {
		HeaderParameter hp = new HeaderParameter();
		
		// set parameterName
		String s = ParserHelper.getAnnotationValue(parameter, getName(), Consts.VALUE);
		hp.setName(ParserHelper.replaceQuotationMarks(s));
		
		// set default value
		String defaultValue = ParameterAnnotationHandler.getDefaultValue(parameter);
		if (defaultValue != null) {
			hp.setDefaultValue(defaultValue);
		}
		
		// set description
		hp.setDescription(ParameterAnnotationHandler.getDescriptionForParameters(methodDoc, parameter));
		
		// set property
		String[] typeAndFormat = ParserHelper.checkTypeAndFormat(parameter.type());
		hp.setProperty(ParserHelper.createProperty(typeAndFormat, parameter.type()));
		
		return hp;
	}
	
	@Override
	public String getName() {
		return name;
	}
}