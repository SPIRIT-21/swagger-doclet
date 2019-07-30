package com.spirit21.spring.handler.parameter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.spirit21.common.handler.parameter.AbstractHandler;
import com.spirit21.common.helper.CommonHelper;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.parameters.Parameter;

/**
 * Factory which creates a swagger (HTTP) parameter out of a JavaDoc parameter.
 * 
 * @author mweidmann
 */
public class ParameterFactory {
	
	private List<AbstractHandler> parameterHandlers = new ArrayList<>();
	
	public ParameterFactory() {
		this.parameterHandlers.add(new BodyParameterHandler(RequestBody.class.getName()));
		this.parameterHandlers.add(new HeaderParameterHandler(RequestHeader.class.getName()));
		this.parameterHandlers.add(new QueryParameterHandler(RequestParam.class.getName()));
	}
	
	/**
	 * Creates a swagger parameter from a JavaDoc parameter. 
	 * It is used to set the parameters of a swagger operation for example.
	 * 
	 * @param methodDoc The MethodDoc which contains the JavaDoc parameter.
	 * @param javaDocParameter The JavaDoc parameter itself.
	 * @return The fully configured swagger parameter.
	 */
	public Parameter createSwaggerParameter(MethodDoc methodDoc, com.sun.javadoc.Parameter javaDocParameter) {
		return parameterHandlers.stream()
				.filter(parameterHandler -> CommonHelper.hasAnnotation(javaDocParameter, parameterHandler.getHttpParameterType()))
				.map(parameterHandler -> parameterHandler.createNewSwaggerParameter(methodDoc, javaDocParameter))
				.findFirst()
				.orElse(null);
	}
}
