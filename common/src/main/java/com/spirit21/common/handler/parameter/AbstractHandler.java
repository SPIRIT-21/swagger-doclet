package com.spirit21.common.handler.parameter;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;

import io.swagger.models.parameters.Parameter;

/**
 * Abstract class from which every AbstractParameterHandler will extend.
 * 
 * @author mweidmann
 */
public abstract class AbstractHandler {
	
	/**
	 * Gets the description for a parameter of the JavaDoc comment of a method.
	 * 
	 * @param methodDoc The MethodDoc which contains the parameter.
	 * @param javaDocParameter The JavaDoc parameter which should be converted to a swagger parameter.
	 * @return The description of the parameter or an empty string if no description was found.
	 */
	protected String getDescriptionForJavaDocParameter(MethodDoc methodDoc, com.sun.javadoc.Parameter javaDocParameter) {
		return Arrays.asList(methodDoc.paramTags()).stream()
				.filter(tag -> tag.parameterName().equals(javaDocParameter.name()))
				.map(Tag::inlineTags)
				.flatMap(Arrays::stream)
				.map(Tag::text)
				.collect(Collectors.joining());
	}
	
	/**
	 * Creates a new Swagger (Query/Body/Path/..)Parameter out of a JavaDoc Parameter.
	 * 
	 * @param methodDoc The MethodDoc which contains the parameter.
	 * @param javaDocParameter The JavaDoc parameter which should be converted to a swagger parameter.
	 * @return The generated Swagger Parameter.
	 */
	public abstract Parameter createNewSwaggerParameter(MethodDoc methodDoc, com.sun.javadoc.Parameter javaDocParameter);

	/**
	 * Get the HTTP parameter type, e.g. Body, Query, Path, ...
	 * 
	 * @return The parameter type as a string.
	 */
	public abstract String getHttpParameterType();
}
