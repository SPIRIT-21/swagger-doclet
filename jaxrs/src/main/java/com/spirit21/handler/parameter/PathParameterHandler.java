package com.spirit21.handler.parameter;

import com.spirit21.Consts;
import com.spirit21.handler.property.PropertyFactory;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.parameters.PathParameter;

public class PathParameterHandler extends AbstractParameterHandler<PathParameter> implements ParameterAnnotationHandler {

	private final String name;

	public PathParameterHandler(String name) {
		this.name = name;
	}
	
	/** 
	 * This method creates a new path parameter sets the data and returns it
	 */
	@Override
	public PathParameter createNewParameter(Parameter parameter, MethodDoc methodDoc) {
		PathParameter pathParameter = new PathParameter();
		handleParameter(pathParameter, parameter, methodDoc);
		return pathParameter;
	}
	
	/**
	 * This method creates a new path parameter of a fieldDoc sets the data and returns it
	 */
	public PathParameter createPathParameterFromField(FieldDoc fieldDoc) {
		PathParameter pathParameter = new PathParameter();
		
		// set name
		AnnotationValue aValue  = ParserHelper.getAnnotationValue(fieldDoc, getName(), Consts.VALUE);
		String value = (String) ParserHelper.getAnnotationValueObject(aValue);
		pathParameter.setName(value);
		
		// set property
		pathParameter.setProperty(PropertyFactory.createProperty(fieldDoc.type()));
		
		return pathParameter;
	}
	
	@Override
	public String getName() {
		return name;
	}
}
