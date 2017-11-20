package com.spirit21.exception;

public class ApiParserException extends SwaggerException {

	private static final long serialVersionUID = 6831541177634755290L;

	public ApiParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApiParserException(String message) {
		super(message);
	}
}