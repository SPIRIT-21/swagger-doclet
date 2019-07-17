package com.spirit21.common.exception;

/**
 * Parent exception class. This exception will be thrown if something goes wrong.
 * 
 * @author mweidmann
 */
public class SwaggerDocletException extends Exception {

	private static final long serialVersionUID = -400117595015479058L;

	public SwaggerDocletException(String message, Throwable cause) {
		super(message, cause);
	}

	public SwaggerDocletException(String message) {
		super(message);
	}
}
