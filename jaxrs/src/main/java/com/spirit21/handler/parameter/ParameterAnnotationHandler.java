package com.spirit21.handler.parameter;

import java.util.Arrays;

import javax.ws.rs.DefaultValue;

import com.spirit21.Consts;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.parameters.Parameter;

public interface ParameterAnnotationHandler {
	
	/**
	 * This method creates a new (Query/Body/Path)Parameter
	 */
	public abstract Parameter createNewParameter(AnnotationDesc annotation, com.sun.javadoc.Parameter parameter,
			MethodDoc methodDoc);
	
	/**
	 * This method gets the javadoc description of the Parameter
	 */
	public static String getDescriptionForParameters(MethodDoc methodDoc, com.sun.javadoc.Parameter parameter) {
		StringBuilder sb = new StringBuilder();
		
		Arrays.asList(methodDoc.paramTags()).stream()
			.filter(tag -> tag.parameterName().equals(parameter.name()))
			.forEach(tag -> {
				Arrays.asList(tag.inlineTags()).forEach(t -> sb.append(t.text()));
			});
		
		return sb.toString();
	}

	/**
	 * This method gets the DefaultValue of the Parameter
	 */
	public static String getDefaultValue(com.sun.javadoc.Parameter parameter) {
		String defaultValue = null;
		
		for (AnnotationDesc annotation : parameter.annotations()) {
			if (annotation.annotationType().toString().equals(DefaultValue.class.getName())) {
				defaultValue = annotation.elementValues()[0].value().toString().replaceAll(Consts.QUOTATION_MARK,
						Consts.EMPTY_STRING);
				return defaultValue;
			}
		}
		return defaultValue;
	}
}
