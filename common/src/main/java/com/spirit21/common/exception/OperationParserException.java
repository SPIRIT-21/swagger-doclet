package com.spirit21.common.exception;

/**
 * Exception which will be thrown if the parsing of operations went wrong.
 * 
 * @author mweidmann
 */
public class OperationParserException extends SwaggerException {

	private static final long serialVersionUID = -7392355797842943736L;

	public OperationParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public OperationParserException(String message) {
		super(message);
	}
}
