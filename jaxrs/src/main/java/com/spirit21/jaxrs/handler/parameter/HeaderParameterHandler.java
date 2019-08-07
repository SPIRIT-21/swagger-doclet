package com.spirit21.jaxrs.handler.parameter;

import javax.ws.rs.DefaultValue;

import com.spirit21.common.CommonConsts;
import com.spirit21.common.handler.parameter.AbstractParameterHandler;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.parameters.HeaderParameter;

public class HeaderParameterHandler extends AbstractParameterHandler<HeaderParameter> {
	
	public HeaderParameterHandler(String name) {
		super(name, DefaultValue.class.getName(), CommonConsts.ANNOTATION_PROPERTY_NAME_VALUE);
	}

	/** 
	 * This method creates a new header parameter sets the data and returns it
	 */
	@Override
	public HeaderParameter createNewSwaggerParameter(Parameter parameter, MethodDoc methodDoc) {
		HeaderParameter headerParameter = new HeaderParameter();
		setDataToParameter(headerParameter, parameter, methodDoc);
		return headerParameter;
	}
}
