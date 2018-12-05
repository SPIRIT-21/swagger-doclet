package com.spirit21.spring.handler.parameter;

import com.spirit21.common.handler.parameter.AbstractBodyParameterHandler;

public class BodyParameterHandler extends AbstractBodyParameterHandler {
	
	private final String name;
	
	public BodyParameterHandler(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
