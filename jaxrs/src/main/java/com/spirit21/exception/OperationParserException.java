package com.spirit21.exception;

public class OperationParserException extends SwaggerException {

	private static final long serialVersionUID = 3373933311052398404L;

	public OperationParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public OperationParserException(String message) {
		super(message);
	}
}