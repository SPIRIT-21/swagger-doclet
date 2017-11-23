package com.spirit21.handler.parameter;

import java.util.Arrays;

import javax.ws.rs.DefaultValue;

import com.spirit21.Consts;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.parameters.Parameter;

public interface ParameterAnnotationHandler {

	// This method creates a new (Query/Body/Path)Parameter
	public abstract Parameter createNewParameter(com.sun.javadoc.Parameter parameter, MethodDoc methodDoc);
	
	public abstract String getName();

	// This method gets the javadoc description of a Parameter
	public static String getDescriptionForParameters(MethodDoc methodDoc, com.sun.javadoc.Parameter parameter) {
		StringBuilder sb = new StringBuilder();

		Arrays.asList(methodDoc.paramTags()).stream()
			.filter(tag -> tag.parameterName().equals(parameter.name()))
			.forEach(tag -> {
					Arrays.asList(tag.inlineTags()).forEach(t -> sb.append(t.text()));
				});

		return sb.toString();
	}

	// This method gets the DefaultValue of the Parameter
	public static String getDefaultValue(com.sun.javadoc.Parameter parameter) {
		return ParserHelper.getAnnotationValue(parameter, DefaultValue.class.getName(), Consts.VALUE);
	}
}
