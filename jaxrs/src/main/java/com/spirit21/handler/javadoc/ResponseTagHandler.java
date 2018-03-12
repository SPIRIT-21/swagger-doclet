package com.spirit21.handler.javadoc;

import java.util.Arrays;

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

	RESPONSE_MESSAGE(Consts.RESPONSE_MESSAGE) {
		/**
		 * This method creates the responseMessage and sets it in the response Object
		 */
		@Override
		public void setResponseData(Response response, Tag tag) {
			StringBuilder responseMessage = new StringBuilder();
			
			Arrays.asList(tag.inlineTags())
				.forEach(t -> responseMessage.append(t.text()));
			
			response.setDescription(responseMessage.toString());
		}
	},
	RESPONSE_SCHEMA(Consts.RESPONSE_SCHEMA) {
		/** 
		 * This method creates a RefProperty and adds the classDoc to the Parser.definitionClassDoc list, 
		 * if the list does not contain the classDoc
		 */
		@Override
		public void setResponseData(Response response, Tag tag) {
			// get the simpleName of the @responseSchema tag
			StringBuilder simpleNameBuilder = new StringBuilder();
			Arrays.asList(tag.inlineTags())
				.forEach(t -> simpleNameBuilder.append(t.text()));
			String simpleName = simpleNameBuilder.toString();
			
			// set value in the refProperty and set it in the response
			RefProperty ref = new RefProperty();
			ref.set$ref(simpleName);
			response.setSchema(ref);
			
			// add to definitions if list does not contain
			ParserHelper.addToEntityList(Parser.classDocCache.findBySimpleName(simpleName));
		}
	},
	RESPONSE_TYPE(Consts.RESPONSE_TYPE) {
		// This method sets the schema of the response to an arrayProperty
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