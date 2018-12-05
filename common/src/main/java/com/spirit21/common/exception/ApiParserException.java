package com.spirit21.common.exception;

public class ApiParserException extends SwaggerException {

	private static final long serialVersionUID = 5474714634617099805L;

	public ApiParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApiParserException(String message) {
		super(message);
	}
}
