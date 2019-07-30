package com.spirit21;

/**
 * Constants for the javadoc2swagger module.
 * 
 * @author mweidmann
 */
public class Consts {

	// Possible Command-line option.
	public static final String CLI_BACKEND_TYPE = "-backend";

	// All possible/supported Backend types.
	public static final String BACKEND_SPRING = "spring";
	public static final String BACKEND_JAXRS = "jaxrs";

	// Possible output format.
	public static final String OUTPUT_FORMAT_YAML = "yaml";

	// Possible/supported swagger version.
	public static final String SWAGGER_VERSION_2 = "2";
	
	/**
	 * Private constructor to hide the implicit public one.
	 */
	private Consts() { }
}
