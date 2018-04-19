package com.spirit21.handler.parameter;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;

import io.swagger.models.parameters.Parameter;

public interface ParameterAnnotationHandler {
	
	/**
	 * This method gets the javadoc description of the Parameter
	 */
	public static String getDescriptionForParameters(MethodDoc methodDoc, com.sun.javadoc.Parameter parameter) {
		return Arrays.asList(methodDoc.paramTags()).stream()
			.filter(tag -> tag.parameterName().equals(parameter.name()))
			.map(Tag::inlineTags)
			.flatMap(Arrays::stream)
			.map(Tag::text)
			.collect(Collectors.joining());
	}
	
	/**
	 * This method creates a new (Query/Body/Path)Parameter
	 */
	public abstract Parameter createNewParameter(com.sun.javadoc.Parameter parameter, MethodDoc methodDoc);
	
	public abstract String getName();
}
