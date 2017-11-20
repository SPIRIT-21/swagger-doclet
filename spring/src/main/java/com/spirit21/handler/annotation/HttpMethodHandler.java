package com.spirit21.handler.annotation;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import io.swagger.models.Operation;
import io.swagger.models.Path;

public enum HttpMethodHandler {

	GET(GetMapping.class.getName()) {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setGet(operation);
		}
	},
	POST(PostMapping.class.getName()) {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setPost(operation);
		}
	},
	PUT(PutMapping.class.getName()) {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setPut(operation);
		}
	},
	DELETE(DeleteMapping.class.getName()) {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setDelete(operation);
		}
	};

	private final String fullName;

	private HttpMethodHandler(String fullName) {
		this.fullName = fullName;
	}

	public String getFullName() {
		return fullName;
	}

	public abstract void setOperationToPath(Path path, Operation operation);
}