package com.spirit21.common.exception;

public class SwaggerException extends Exception {

	private static final long serialVersionUID = -400117595015479058L;

	public SwaggerException(String message, Throwable cause) {
		super(message, cause);
	}

	public SwaggerException(String message) {
		super(message);
	}
}
