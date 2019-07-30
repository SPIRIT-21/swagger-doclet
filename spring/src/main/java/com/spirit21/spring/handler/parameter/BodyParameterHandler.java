package com.spirit21.spring.handler.parameter;

import com.spirit21.common.handler.parameter.AbstractBodyParameterHandler;

/**
 * Body parameter handler for Spring Boot's body parameters.
 * 
 * @author mweidmann
 */
public class BodyParameterHandler extends AbstractBodyParameterHandler {
	
	private final String httpParameterName;
	
	public BodyParameterHandler(String httpParameterName) {
		this.httpParameterName = httpParameterName;
	}

	@Override
	public String getHttpParameterType() {
		return httpParameterName;
	}
}
