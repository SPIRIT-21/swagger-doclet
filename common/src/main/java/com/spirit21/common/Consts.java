package com.spirit21.common;

public class Consts {

	// Commandline options
	public static final String OUTPUT_TYPE = "-type";

	// ApiParser constants
	public static final String API_TITLE = "@apiTitle";
	public static final String API_DESCRIPTION = "@apiDescription";
	public static final String API_HOST = "@apiHost";
	public static final String API_BASE_PATH = "@apiBasePath";
	public static final String API_VERSION = "@apiVersion";
	public static final String FILE_NAME = "@fileName";

	// OperationParser constants
	public static final String RESPONSE_CODE = "@responseCode";
	public static final String RESPONSE_MESSAGE = "@responseMessage";
	public static final String RESPONSE_SCHEMA = "@responseSchema";
	public static final String RESPONSE_TYPE = "@responseType";

	// Default file name
	public static final String STANDARD_FILE_NAME = "generated-swagger-file";

	// Property constants
	public static final String REF = "ref";
	public static final String ENUM = "enum";
	public static final String ARRAY = "array";
	public static final String MAP = "map";

	// TypeHandler constants
	public static final String STRING = "string";
	public static final String DATE = "date";
	public static final String DATE_TIME = "date-time";
	public static final String NUMBER = "number";
	public static final String INTEGER = "integer";
	public static final String OBJECT = "object";

	// Annotation constants
	public static final String VALUE = "value";
	
	// Regex constants
	public static final String SLASHES = "[/]{2,}";
	public static final String SLASHES_REPLACE = "/";
}
