package com.spirit21.handler.annotation;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import io.swagger.models.Operation;
import io.swagger.models.Path;

// This enum saves all HttpMethods and sets the correct operation to path
public enum HttpMethodHandler {
	
	GET(GET.class.getName(), GET.class.getSimpleName()) {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setGet(operation);
		}
	},
	POST(POST.class.getName(), POST.class.getSimpleName()) {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setPost(operation);
		}
	},
	PUT(PUT.class.getName(), PUT.class.getSimpleName()) {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setPut(operation);
		}
	},
	DELETE(DELETE.class.getName(), DELETE.class.getSimpleName()) {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setDelete(operation);
		}
	},
	HEAD(HEAD.class.getName(), HEAD.class.getSimpleName()) {
		@Override
		public void setOperationToPath(Path path, Operation operation) {
			path.setHead(operation);
		}
	},
	OPTIONS(OPTIONS.class.getName(), OPTIONS.class.getSimpleName()) {
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
