package com.spirit21.common;

public class Consts {

	// All possible Command-line options.
	public static final String CLI_OUTPUT_FORMAT = "-type";
	public static final String CLI_SWAGGER_VERSION = "-version";
	public static final String CLI_BACKEND_TYPE = "-backend";
	public static final String CLI_FILE_NAME = "-filename";

	// Default file name.
	public static final String DEFAULT_FILE_NAME = "generated-swagger-file";

	// All possible output formats.
	public static final String OUTPUT_FORMAT_JSON = "json";
	public static final String OUTPUT_FORMAT_YAML = "yaml";

	// All possible/supported swagger versions.
	public static final String SWAGGER_VERSION_2 = "2";
	public static final String SWAGGER_VERSION_3 = "3";

	// All possible/supported Backend types.
	public static final String BACKEND_SPRING = "spring";
	public static final String BACKEND_JAXRS = "jaxrs";

	// ApiParser constants for the entry point ClassDoc.
	// These tags are used in the JavaDoc of the entry point ClassDoc of the REST API.
	public static final String API_PARSER_TITLE = "@apiTitle";
	public static final String API_PARSER_DESCRIPTION = "@apiDescription";
	public static final String API_PARSER_HOST = "@apiHost";
	public static final String API_PARSER_BASE_PATH = "@apiBasePath";
	public static final String API_PARSER_VERSION = "@apiVersion";
	public static final String API_PARSER_FILE_NAME = "@fileName";

	// DataTypeHandler constants for the typeAndFormat array.
	// This array describes the type and format of property in swagger format.
	public static final String DATA_TYPE_STRING = "string";
	public static final String DATA_TYPE_DATE = "date";
	public static final String DATA_TYPE_DATE_TIME = "date-time";
	public static final String DATA_TYPE_NUMBER = "number";
	public static final String DATA_TYPE_INTEGER = "integer";
	public static final String DATA_TYPE_OBJECT = "object";
	public static final String DATA_TYPE_BOOLEAN = "boolean";

	// OperationParser constants for every MethodDoc of a resource method.
	// These tags are used in the JavaDoc of the method which contains one API endpoint.
	public static final String OPERATION_PARSER_RESPONSE_CODE = "@responseCode";
	public static final String OPERATION_PARSER_RESPONSE_MESSAGE = "@responseMessage";
	public static final String OPERATION_PARSER_RESPONSE_SCHEMA = "@responseSchema";
	public static final String OPERATION_PARSER_RESPONSE_TYPE = "@responseType";

	// PropertyFactory constants for every property which is not supported in the DataTypeHandler.
	// These types are needed to add a custom support for some data types, e.g. Maps, Enums, Arrays...
	public static final String PROPERTY_TYPE_REF = "ref";
	public static final String PROPERTY_TYPE_ENUM = "enum";
	public static final String PROPERTY_TYPE_ARRAY = "array";
	public static final String PROPERTY_TYPE_MAP = "map";

	// Common names of properties of annotations. 
	public static final String ANNOTATION_PROPERTY_NAME_VALUE = "value";

	// Common regular expressions. These expressions are used to replace multiple successive slashes with one.
	public static final String REGEX_SLASHES = "[/]{2,}";
	public static final String REGEX_SLASHES_REPLACE = "/";
}
