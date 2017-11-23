package com.spirit21.handler.parameter;

import com.spirit21.Consts;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.parameters.PathParameter;

public class PathParameterHandler implements ParameterAnnotationHandler {

	private final String name;

	public PathParameterHandler(String name) {
		this.name = name;
	}
	
	// This method creates a new PathParameter sets the data and returns it
	@Override
	public PathParameter createNewParameter(Parameter parameter, MethodDoc methodDoc) {
		PathParameter pp = new PathParameter();
		
		//set parameter name
		String s = ParserHelper.getAnnotationValue(parameter, getName(), Consts.VALUE);
		pp.setName(ParserHelper.replaceQuotationMarks(s));

		// set defaultValue
		String defaultValue = ParameterAnnotationHandler.getDefaultValue(parameter);
		if (defaultValue != null) {
			pp.setDefaultValue(defaultValue);
		}
		
		//set description
		pp.setDescription(ParameterAnnotationHandler.getDescriptionForParameters(methodDoc, parameter));
		
		// set property
		String[] typeAndFormat = ParserHelper.checkTypeAndFormat(parameter.type());
		pp.setProperty(ParserHelper.createProperty(typeAndFormat, parameter.type()));
		
		return pp;
	}
	
	// This method creates a new PathParameter of a FieldDoc sets the data and returns it
	public PathParameter createPathParameterFromField(FieldDoc fieldDoc) {
		PathParameter pp = new PathParameter();
		
		// set name
		String s = ParserHelper.getAnnotationValue(fieldDoc, getName(), Consts.VALUE);
		pp.setName(ParserHelper.replaceQuotationMarks(s));
		
		// set property
		String[] typeAndFormat = ParserHelper.checkTypeAndFormat(fieldDoc.type());
		pp.setProperty(ParserHelper.createProperty(typeAndFormat, fieldDoc.type()));
		
		return pp;
	}
	
	@Override
	public String getName() {
		return name;
	}
}
