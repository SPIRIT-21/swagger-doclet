package com.spirit21.handler.parameter;

import com.spirit21.Consts;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.parameters.FormParameter;

public class FormParameterHandler implements ParameterAnnotationHandler {
	
	private final String name;
	
	public FormParameterHandler(String name) {
		this.name = name;
	}
	
	@Override
	public FormParameter createNewParameter(Parameter parameter, MethodDoc methodDoc) {
		FormParameter fp = new FormParameter();
		
		//set parameter name
		String s = ParserHelper.getAnnotationValue(parameter, getName(), Consts.VALUE);
		fp.setName(ParserHelper.replaceQuotationMarks(s));
		
		// set default value
		String defaultValue = ParameterAnnotationHandler.getDefaultValue(parameter);
		if (defaultValue != null) {
			fp.setDefaultValue(defaultValue);
		}
		
		// set description
		fp.setDescription(ParameterAnnotationHandler.getDescriptionForParameters(methodDoc, parameter));
		
		// set property
		String[] typeAndFormat = ParserHelper.checkTypeAndFormat(parameter.type());
		fp.setProperty(ParserHelper.createProperty(typeAndFormat, parameter.type()));
		
		return fp;
	}
	
	@Override
	public String getName() {
		return name;
	}
}