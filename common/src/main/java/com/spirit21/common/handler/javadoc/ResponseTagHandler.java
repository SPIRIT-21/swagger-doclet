package com.spirit21.common.handler.javadoc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.spirit21.common.Consts;
import com.spirit21.common.helper.ClassDocCache;
import com.spirit21.common.helper.CommonHelper;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;

import io.swagger.models.Response;
import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.RefProperty;

/**
 * Handles the tags of the JavaDoc comment of a response of an interface.
 * Then it saves the values in the Response model.
 * 
 * @author mweidmann
 */
// TODO: Refactor deprecated methods.
// TODO: Static access to ClassDocCache and definitions.
public enum ResponseTagHandler {
	
	/**
	 * Creates the response message and set it into the Response object.
	 */
	RESPONSE_MESSAGE(Consts.RESPONSE_MESSAGE) {
		@Override
		public void setTagValueToSwaggerModel(Response response, Tag tag, List<ClassDoc> definitions, ClassDocCache cache) {
			String responseMessage = Arrays.asList(tag.inlineTags()).stream()
					.map(Tag::text)
					.collect(Collectors.joining());
			
			response.setDescription(responseMessage);
		}
	},
	/**
	 * Creates a RefProperty and adds the classDoc to the Parser.defintionClassDoc list.
	 */
	RESPONSE_SCHEMA(Consts.RESPONSE_SCHEMA) {
		@Override
		public void setTagValueToSwaggerModel(Response response, Tag tag, List<ClassDoc> definitions, ClassDocCache cache) {
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
	 * Creates an ArrayProperty and set the schema of the response to it.
	 * Then the schema of the response will be overwritten with the created ArrayProperty.
	 */
	RESPONSE_TYPE(Consts.RESPONSE_TYPE) {
		@Override
		public void setTagValueToSwaggerModel(Response response, Tag tag, List<ClassDoc> definitions, ClassDocCache cache) {
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

	public abstract void setTagValueToSwaggerModel(Response response, Tag tag, List<ClassDoc> definitions, ClassDocCache cache);
}
