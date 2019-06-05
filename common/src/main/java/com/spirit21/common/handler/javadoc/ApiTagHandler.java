package com.spirit21.common.handler.javadoc;

import com.spirit21.common.Consts;

import io.swagger.models.Info;
import io.swagger.models.Swagger;

/**
 * Handles the tags of the JavaDoc comment of the entry point of the REST API.
 * Then it saves the values in the Swagger and Info model.
 * 
 * @author mweidmann
 */
public enum ApiTagHandler {

	API_TITLE(Consts.API_TITLE) {
		@Override
		public void setTagValueToSwaggerModel(Swagger swagger, Info info, String value) {
			info.setTitle(value);
		}
	},
	API_DESCRIPTION(Consts.API_DESCRIPTION) {
		@Override
		public void setTagValueToSwaggerModel(Swagger swagger, Info info, String value) {
			info.setDescription(value);
		}
	},
	API_HOST(Consts.API_HOST) {
		@Override
		public void setTagValueToSwaggerModel(Swagger swagger, Info info, String value) {
			swagger.setHost(value);
		}
	},
	API_BASE_PATH(Consts.API_BASE_PATH) {
		@Override
		public void setTagValueToSwaggerModel(Swagger swagger, Info info, String value) {
			swagger.setBasePath(value);
		}
	},
	API_VERSION(Consts.API_VERSION) {
		@Override
		public void setTagValueToSwaggerModel(Swagger swagger, Info info, String value) {
			info.setVersion(value);
		}
	};

	private final String tagName;

	private ApiTagHandler(String tagName) {
		this.tagName = tagName;
	}

	public String getTagName() {
		return tagName;
	}

	public abstract void setTagValueToSwaggerModel(Swagger swagger, Info info, String value);
}
