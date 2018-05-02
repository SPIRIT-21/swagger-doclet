package com.spirit21.common.handler.javadoc;

import com.spirit21.common.Consts;

import io.swagger.models.Info;
import io.swagger.models.Swagger;

/**
 * This enum saves the values of the javadoc comment of the entry point of the REST API
 * in the swagger/info model
 */
public enum ApiTagHandler {

	API_TITLE(Consts.API_TITLE) {
		@Override
		public void setValue(Swagger swagger, Info info, String value) {
			info.setTitle(value);
		}
	},
	API_DESCRIPTION(Consts.API_DESCRIPTION) {
		@Override
		public void setValue(Swagger swagger, Info info, String value) {
			info.setDescription(value);
		}
	},
	API_HOST(Consts.API_HOST) {
		@Override
		public void setValue(Swagger swagger, Info info, String value) {
			swagger.setHost(value);
		}
	},
	API_BASE_PATH(Consts.API_BASE_PATH) {
		@Override
		public void setValue(Swagger swagger, Info info, String value) {
			swagger.setBasePath(value);
		}
	},
	API_VERSION(Consts.API_VERSION) {
		@Override
		public void setValue(Swagger swagger, Info info, String value) {
			info.setVersion(value);
		}
	};

	private final String name;

	private ApiTagHandler(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract void setValue(Swagger swagger, Info info, String value);
}
