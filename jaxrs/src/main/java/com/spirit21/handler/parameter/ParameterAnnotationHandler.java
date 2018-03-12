package com.spirit21.handler.parameter;

import java.util.Arrays;

import com.sun.javadoc.MethodDoc;

import io.swagger.models.parameters.Parameter;

public interface ParameterAnnotationHandler {
	
	/**
	 * This method gets the javadoc description of the Parameter
	 */
	public static String getDescriptionForParameters(MethodDoc methodDoc, com.sun.javadoc.Parameter parameter) {
		StringBuilder sb = new StringBuilder();

		Arrays.asList(methodDoc.paramTags()).stream()
			.filter(tag -> tag.parameterName().equals(parameter.name()))
			.flatMap(tag -> Arrays.asList(tag.inlineTags()).stream())
			.map(inlineTag -> inlineTag.text())
			.forEach(sb::append);
		
		return sb.toString();
	}
	
	/**
	 * This method creates a new (Query/Body/Path)Parameter
	 */
	public abstract Parameter createNewParameter(com.sun.javadoc.Parameter parameter, MethodDoc methodDoc);
	
	public abstract String getName();
}
