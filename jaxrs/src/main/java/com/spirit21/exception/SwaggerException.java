package com.spirit21.exception;

public class SwaggerException extends Exception {

	private static final long serialVersionUID = -5058119381383284890L;

	public SwaggerException(String message, Throwable cause) {
		super(message, cause);
	}

	public SwaggerException(String message) {
		super(message);
	}
}