package com.spirit21.common.handler.javadoc;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.spirit21.common.Consts;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.common.parser.AbstractParser;
import com.sun.javadoc.Tag;

import io.swagger.models.Response;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.RefProperty;

/**
 * Handles the tags of the JavaDoc comment of a response.
 * Then it saves the values in the Response model.
 * 
 * @author mweidmann
 */
// TODO: Refactor deprecated methods.
public enum ResponseTagHandler {
	
	/**
	 * Parses the response message out of the JavaDoc tag. Then the response message will be set.
	 * 
	 * @author mweidmann
	 */
	RESPONSE_MESSAGE_TAG(Consts.OPERATION_PARSER_RESPONSE_MESSAGE) {
		@Override
		public void setTagValueToResponseModel(Tag tag, Response response) {
			String responseMessage = Arrays.asList(tag.inlineTags()).stream()
					.map(Tag::text)
					.collect(Collectors.joining());
			
			response.setDescription(responseMessage);
		}
	},
	/**
	 * Creates a RefProperty out the parsed class name from the JavaDoc tag.
	 * Then the parsed class will be added to the Parser.defintionClassDoc list.
	 * 
	 * @author mweidmann
	 */
	RESPONSE_SCHEMA_TAG(Consts.OPERATION_PARSER_RESPONSE_SCHEMA) {
		@Override
		public void setTagValueToResponseModel(Tag tag, Response response) {
			String simpleName = Arrays.asList(tag.inlineTags()).stream()
						.map(Tag::text)
						.collect(Collectors.joining());
			
			RefProperty refProperty = new RefProperty();
			refProperty.set$ref(simpleName);
			response.setSchema(refProperty);
			
			CommonHelper.addToDefinitionList(AbstractParser.CLASS_DOC_CACHE.findBySimpleName(simpleName));
		}
	},
	/**
	 * Creates an ArrayProperty, configures it and sets the schema of the response to the property.
	 * 
	 * @author mweidmann
	 */
	RESPONSE_TYPE_TAG(Consts.OPERATION_PARSER_RESPONSE_TYPE) {
		@Override
		public void setTagValueToResponseModel(Tag tag, Response response) {
			if (response.getSchema() != null) {
				ArrayProperty arrayProperty = new ArrayProperty();
				arrayProperty.setItems(response.getSchema());
				response.setSchema(arrayProperty);
			}
		}
	};

	private final String tagName;

	private ResponseTagHandler(String tagName) {
		this.tagName = tagName;
	}

	public String getTagName() {
		return tagName;
	}
	
	/**
	 * Parses the JavaDoc comment tag and puts the tag value into the swagger response model.
	 * 
	 * @param tag The JavaDoc comment tag out of which the information will be parsed.
	 * @param response The swagger response model in which the information will be set.
	 */
	public abstract void setTagValueToResponseModel(Tag tag, Response response);
}
