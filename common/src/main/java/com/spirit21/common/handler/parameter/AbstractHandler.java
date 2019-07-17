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
	 * The parameter for this specific handler.
	 */
	protected com.sun.javadoc.Parameter javaDocParameter;

	/**
	 * The method which contains the parameter.
	 */
	protected MethodDoc methodDoc;

	public AbstractHandler(MethodDoc methodDoc, com.sun.javadoc.Parameter javaDocParameter) {
		this.methodDoc = methodDoc;
		this.javaDocParameter = javaDocParameter;
	}

	/**
	 * Gets the description for a parameter of the JavaDoc comment of a method.
	 * 
	 * @return The description of the parameter or an empty string if no description
	 *         was found.
	 */
	protected String getDescriptionForJavaDocParameter() {
		return Arrays.asList(methodDoc.paramTags()).stream()
				.filter(tag -> tag.parameterName().equals(javaDocParameter.name()))
				.map(Tag::inlineTags)
				.flatMap(Arrays::stream)
				.map(Tag::text)
				.collect(Collectors.joining());
	}
	
	/**
	 * Creates a new Swagger (Query/Body/Path/..)Parameter out of a DocletParameter.
	 * 
	 * @return The generated Swagger Parameter.
	 */
	public abstract Parameter createNewSwaggerParameter();

	/**
	 * Get the HTTP parameter type, e.g. Body, Query, Path, ...
	 * 
	 * @return The parameter type as a string.
	 */
	public abstract String getHttpParameterType();
}
