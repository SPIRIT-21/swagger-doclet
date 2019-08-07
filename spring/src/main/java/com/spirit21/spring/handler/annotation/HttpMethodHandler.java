package com.spirit21.spring.handler.annotation;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import io.swagger.models.Operation;
import io.swagger.models.Path;

/**
 * Handles all possible Spring Boot HTTP methods by setting a swagger operation to a path.
 * 
 * @author mweidmann
 */
// TODO: maybe create consts variables for the first parameter?
public enum HttpMethodHandler {

	GET("get", GetMapping.class.getName()) {
		@Override
		public void setOperationToPath(Operation operation, Path swaggerPath) {
			swaggerPath.setGet(operation);
		}
	},
	POST("post", PostMapping.class.getName()) {
		@Override
		public void setOperationToPath(Operation operation, Path swaggerPath) {
			swaggerPath.setPost(operation);
		}
	},
	PUT("put", PutMapping.class.getName()) {
		@Override
		public void setOperationToPath(Operation operation, Path swaggerPath) {
			swaggerPath.setPut(operation);
		}
	},
	DELETE("delete", DeleteMapping.class.getName()) {
		@Override
		public void setOperationToPath(Operation operation, Path swaggerPath) {
			swaggerPath.setDelete(operation);
		}
	},
	PATCH("patch", PatchMapping.class.getName()) {
		@Override
		public void setOperationToPath(Operation operation, Path swaggerPath) {
			swaggerPath.setPatch(operation);
		}
	},
	HEAD("head", "") {
		@Override
		public void setOperationToPath(Operation operation, Path swaggerPath) {
			swaggerPath.setHead(operation);
		}
	},
	OPTIONS("options", "") {
		@Override
		public void setOperationToPath(Operation operation, Path swaggerPath) {
			swaggerPath.setOptions(operation);
		}
	};

	private final String simpleHttpMethodName;
	private final String fullHttpMethodName;
	
	private HttpMethodHandler(String simpleHttpMethodName, String fullHttpMethodName) {
		this.simpleHttpMethodName = simpleHttpMethodName;
		this.fullHttpMethodName = fullHttpMethodName;
	}
	
	public String getSimpleHttpMethodName() {
		return simpleHttpMethodName;
	}
	
	public String getFullHttpMethodName() {
		return fullHttpMethodName;
	}

	/**
	 * Adds the passed fully configured swagger operation to the passed swagger path.
	 * 
	 * @param operation The operation which should be set to the path object.
	 * @param swaggerPath The swagger path in which the operation should be set.
	 */
	public abstract void setOperationToPath(Operation operation, Path swaggerPath);
}
