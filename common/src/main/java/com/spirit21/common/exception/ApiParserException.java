package com.spirit21.common.exception;

/**
 * Exception which will be thrown if the parsing of the JavaDoc of the entry point class went wrong.
 * 
 * @author mweidmann
 */
public class ApiParserException extends SwaggerException {

	private static final long serialVersionUID = 5474714634617099805L;

	public ApiParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApiParserException(String message) {
		super(message);
	}
}
