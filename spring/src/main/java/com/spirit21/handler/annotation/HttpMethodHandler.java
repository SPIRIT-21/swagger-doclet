package com.spirit21.handler.annotation;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import io.swagger.models.Operation;
import io.swagger.models.Path;

/**
 * This method saves all HttpMethods and sets the correct operation to path
 */
public enum HttpMethodHandler {

	GET(GetMapping.class.getName(), "get") {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setGet(operation);
		}
	},
	POST(PostMapping.class.getName(), "post") {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setPost(operation);
		}
	},
	PUT(PutMapping.class.getName(), "put") {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setPut(operation);
		}
	},
	DELETE(DeleteMapping.class.getName(), "delete") {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setDelete(operation);
		}
	},
	PATCH(PatchMapping.class.getName(), "patch") {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setPatch(operation);
		}
	},
	HEAD("", "head") {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setHead(operation);
		}
	},
	OPTIONS("", "options") {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setOptions(operation);
		}
	};

	private final String fullName;
	private final String simpleName;
	
	private HttpMethodHandler(String fullName, String simpleName) {
		this.fullName = fullName;
		this.simpleName = simpleName;
	}
	
	public String getFullName() {
		return fullName;
	}

	public String getSimpleName() {
		return simpleName;
	}

	public abstract void setOperationToPath(Path path, Operation operation);
}
