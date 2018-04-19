package com.spirit21.handler.javadoc;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.spirit21.Consts;
import com.spirit21.helper.ParserHelper;
import com.spirit21.parser.Parser;
import com.sun.javadoc.Tag;

import io.swagger.models.Response;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.RefProperty;

/**
 * This enum saves the response-tags of the javadoc comment of the HttpMethods
 * and sets the value to the response
 */
public enum ResponseTagHandler {
	
	/**
	 * This value creates the response message and sets it in the response object
	 */
	RESPONSE_MESSAGE(Consts.RESPONSE_MESSAGE) {
		@Override
		public void setResponseData(Response response, Tag tag) {
			String responseMessage = Arrays.asList(tag.inlineTags()).stream()
						.map(Tag::text)
						.collect(Collectors.joining());
			
			response.setDescription(responseMessage);
		}
	},
	/** 
	 * This value creates a RefProperty and adds the classDoc to the Parser.definitionClassDoc list, 
	 * if the list does not contain the classDoc
	 */
	RESPONSE_SCHEMA(Consts.RESPONSE_SCHEMA) {
		@Override
		public void setResponseData(Response response, Tag tag) {
			String simpleName = Arrays.asList(tag.inlineTags()).stream()
						.map(Tag::text)
						.collect(Collectors.joining());
			
			RefProperty ref = new RefProperty();
			ref.set$ref(simpleName);
			response.setSchema(ref);
			
			ParserHelper.addToDefinitionList(Parser.classDocCache.findBySimpleName(simpleName));
		}
	},
	/**
	 * If the response type tag exists, this value creates an array property
	 * set its properties and give it to the response object 
	 */
	RESPONSE_TYPE(Consts.RESPONSE_TYPE) {
		@Override
		public void setResponseData(Response response, Tag tag) {
			if (response.getSchema() != null) {
				ArrayProperty array = new ArrayProperty();
				array.setItems(response.getSchema());
				response.setSchema(array);
			}
		}
	};

	private final String name;

	private ResponseTagHandler(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract void setResponseData(Response response, Tag tag);
}