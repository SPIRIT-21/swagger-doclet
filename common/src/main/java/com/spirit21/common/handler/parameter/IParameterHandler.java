package com.spirit21.common.handler.parameter;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;

import io.swagger.models.parameters.Parameter;

/**
 * Interface which will be implemented in every AbstractParameterHandler.
 * 
 * @author mweidmann
 */
// TODO: refactor this interface to an abstract class? if yes, update also javadoc.
public interface IParameterHandler {

	/**
	 * Gets the description of a parameter from the JavaDoc comment of a method.
	 * This method is implemented in interface because every class which implements this Interface needs this method.
	 * 
	 * @param methodDoc The method which contains the parameter and the JavaDoc comment.
	 * @param parameter The parameter for which the description of the parameter is to be searched.
	 * @return The description of the parameter or an empty string if no description was found.
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
	 * Creates a new Swagger (Query/Body/Path/..)Parameter out of an DocletParameter.
	 * 
	 * @param parameter The DocletParameter.
	 * @param methodDoc The method which contains the parameter.
	 * @return The generated Swagger Parameter.
	 */
	public abstract Parameter createNewSwaggerParameter(com.sun.javadoc.Parameter parameter, MethodDoc methodDoc);
	
	/**
	 * Get the HTTP parameter type, e.g. Body, Query, Path, ...
	 * 
	 * @return The parameter type as a string.
	 */
	public abstract String getHttpParameterType();
}
