package com.spirit21.jaxrs.handler.parameter;

import javax.ws.rs.DefaultValue;

import com.spirit21.common.Consts;
import com.spirit21.common.handler.parameter.AbstractParameterHandler;
import com.spirit21.common.handler.property.PropertyFactory;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.jaxrs.parser.Parser;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import v2.io.swagger.models.parameters.PathParameter;

public class PathParameterHandler extends AbstractParameterHandler<PathParameter> {

	public PathParameterHandler(String name) {
		super(name, DefaultValue.class.getName(), Consts.VALUE);
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
		AnnotationValue aValue  = CommonHelper.getAnnotationValue(fieldDoc, getName(), Consts.VALUE);
		String value = (String) CommonHelper.getAnnotationValueObject(aValue);
		pathParameter.setName(value);
		
		// set property
		pathParameter.setProperty(PropertyFactory.createProperty(fieldDoc.type(), Parser.definitionClassDocs, Parser.classDocCache));
		
		return pathParameter;
	}
}
