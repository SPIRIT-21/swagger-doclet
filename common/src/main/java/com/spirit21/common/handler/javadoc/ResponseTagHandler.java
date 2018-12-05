package com.spirit21.common.handler.javadoc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.spirit21.common.Consts;
import com.spirit21.common.helper.ClassDocCache;
import com.spirit21.common.helper.CommonHelper;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;

import v2.io.swagger.models.Response;
import v2.io.swagger.models.properties.ArrayProperty;
import v2.io.swagger.models.properties.RefProperty;

/**
 * This enum saves the response tags of the javadoc comment of the http methods
 * and sets the value to the response
 */
public enum ResponseTagHandler {
	
	/**
	 * This value creates the response message and sets it in the response object
	 */
	RESPONSE_MESSAGE(Consts.RESPONSE_MESSAGE) {
		@Override
		public void setResponseData(Response response, Tag tag, List<ClassDoc> definitions, ClassDocCache cache) {
			String responseMessage = Arrays.asList(tag.inlineTags()).stream()
					.map(Tag::text)
					.collect(Collectors.joining());
			
			response.setDescription(responseMessage);
		}
	},
	/**
	 * This value creates a RefProperty and adds the classDoc to the Parser.definitionClassDoc list
	 * if the list does not contain the classDoc
	 */
	RESPONSE_SCHEMA(Consts.RESPONSE_SCHEMA) {
		@Override
		public void setResponseData(Response response, Tag tag, List<ClassDoc> definitions, ClassDocCache cache) {
			String simpleName = Arrays.asList(tag.inlineTags()).stream()
						.map(Tag::text)
						.collect(Collectors.joining());
			
			RefProperty ref = new RefProperty();
			ref.set$ref(simpleName);
			response.setSchema(ref);
			
			CommonHelper.addToDefinitionList(definitions, cache.findBySimpleName(simpleName));
		}
	},
	/**
	 * If the response type tag exists, this value creates an array property, 
	 * set its properties and set it in the response model
	 */
	RESPONSE_TYPE(Consts.RESPONSE_TYPE) {
		@Override
		public void setResponseData(Response response, Tag tag, List<ClassDoc> definitions, ClassDocCache cache) {
			if (response.getSchema() != null) {
				ArrayProperty arrayProperty = new ArrayProperty();
				arrayProperty.setItems(response.getSchema());
				response.setSchema(arrayProperty);
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

	public abstract void setResponseData(Response response, Tag tag, List<ClassDoc> definitions, ClassDocCache cache);
}
