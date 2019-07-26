package com.spirit21.common.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spirit21.common.CommonConsts;
import com.spirit21.common.exception.ApiParserException;
import com.spirit21.common.helper.ClassDocCache;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

import io.swagger.models.Swagger;
import io.swagger.parser.util.SwaggerDeserializationResult;
import io.swagger.util.Json;
import io.swagger.util.Yaml;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.converter.SwaggerConverter;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

/**
 * Abstract class from which every "root"-parser will inherit.
 * 
 * @author mweidmann
 */
public abstract class AbstractParser {
	
	// This pattern finds the tag name out of an API endpoint. Rule: The root/first resource in the path is the tag name.
	// E.g. with the endpoint "/LALA/xfa/d1" this pattern finds "LALA".
	public static final Pattern PATTERN_TAG_NAME = Pattern.compile("\"?/?([a-zA-Z0-9_-]+)/?.*\"?");
	public static final List<ClassDoc> DEFINITION_CLASS_DOCS = new ArrayList<>();
	public static final ClassDocCache CLASS_DOC_CACHE = new ClassDocCache();
	
	protected static ClassDoc entryPointClassDoc;
	
	protected RootDoc rootDoc;
	protected Swagger swagger;
	protected ApiParser apiParser;
	protected DefinitionParser definitionParser;
	
	private Map<String, String> arguments;
	
	public AbstractParser(RootDoc rootDoc, Map<String, String> arguments) {
		CLASS_DOC_CACHE.init(Arrays.asList(rootDoc.classes()));
		
		this.rootDoc = rootDoc;
		this.swagger = new Swagger();
		this.apiParser = new ApiParser();
		this.definitionParser = new DefinitionParser();
		
		this.arguments = arguments;
	}
	
	/**
	 * Starts the conversion process. Firstly, the ClassDocs will be filtered and sorted in different data structures.
	 * After that the information will be parsed and the Swagger object will be filled with information.
	 * The order of information parsing looks as follows:
	 * 	1) ApiParser - The entry point ClassDoc will be parsed for information.
	 * 				   After that the information will be stored in the Swagger object.
	 * 	2) TagParser - The tags for every top-level resource will be created and saved.
	 * 	3) PathParser - Every path with every operation will be created and saved.
	 * 	4) DefinitionParser - Lastly, the definitions for data types will be created and stored in the Swagger model.
	 * The last step is to create a file and save the stringified Swagger object in the file.
	 * 
	 * @return True if the conversion process ran without any problems. Otherwise false.
	 * @throws ApiParserException if the parsing of the entry point went wrong.
	 * @throws IOException if during the creation or writing of the resulting file something went wrong.
	 */
	public abstract boolean run() throws ApiParserException, IOException;
	
	/**
	 * Gets the ClassDoc for the entry point of the REST API. If more than one entry point was found
	 * or no entry point exists, an exception will be thrown.
	 * 
	 * @param predicate The condition by which the ClassDocs are filtered.
	 * @return The found entry point ClassDoc.
	 * @throws ApiParserException if more than one entry point was found or no entry point exists.
	 */
	protected ClassDoc getEntryPointClassDoc(Predicate<? super ClassDoc> predicate) throws ApiParserException {
		List<ClassDoc> tmpList = Arrays.asList(rootDoc.classes()).stream()
				.filter(predicate)
				.collect(Collectors.toList());
		
		if (tmpList.size() == 1) {
			return tmpList.get(0);
		} else if (tmpList.size() > 1) {
			throw new ApiParserException("Mutliple API entry points found. Only one entry point is allowed.");
		} else {
			throw new ApiParserException("Your API does not have any entry point. Please specify one entry point.");
		}
	}
	
	/**
	 * Tries to create a new file.
	 * 
	 * @param fileName The name of the file which should be created.
	 * @return The File object of the newly created file.
	 * @throws IOException if the creation of the file fails.
	 */
	private File createFile(String fileName) throws IOException {
		File file = new File(fileName + "." + arguments.get(CommonConsts.CLI_OUTPUT_FORMAT));
		
		if (file.createNewFile()) {
			return file;
		} else {
			throw new IOException("Could not create the swagger file.");
		}
	}

	/**
	 * Stringifies the Swagger v2 object to JSON or YAML. After that a file will be created
	 * and the stringified object will be written into it.
	 * 
	 * @param fileName The name of the file.
	 * @throws IOException if during writing into the file something goes wrong.
	 */
	protected void writeFile(String fileName) throws IOException {
		String output = null;
		
		if (arguments.get(CommonConsts.CLI_SWAGGER_VERSION).equals(CommonConsts.SWAGGER_VERSION_3)) {
			output = getSwaggerV3String();
		} else {
			output = getSwaggerV2String();
		}
		
		File file = createFile(fileName);

		try (FileWriter fw = new FileWriter(file)) {
			fw.write(output);
		} catch (IOException e) {
			throw new IOException("Could not write into the swagger file.", e);
		}
	}
	
	/**
	 * Stringifies the Swagger v2 object and returns it.
	 * 
	 * @return The stringified Swagger v2 object as JSON or YAML.
	 * @throws JsonProcessingException if the stringification to YAML fails.
	 */
	private String getSwaggerV2String() throws JsonProcessingException {
		if (arguments.get(CommonConsts.CLI_OUTPUT_FORMAT).equals(CommonConsts.OUTPUT_FORMAT_JSON)) {
			return Json.pretty(swagger);
		} else {
			return Yaml.pretty().writeValueAsString(swagger);
		}
	}
	
	/**
	 * Converts the Swagger v2 object to an OpenAPI v3 object. After the conversion 
	 * the object will be stringified. The string will be returned.
	 * 
	 * @return The stringified OpenAPI v3 object as JSON or YAML.
	 * @throws JsonProcessingException if the stringification to YAML fails. 
	 */
	private String getSwaggerV3String() throws JsonProcessingException {
		SwaggerDeserializationResult swaggerDeserializationResult = new SwaggerDeserializationResult();
		swaggerDeserializationResult.setSwagger(swagger);
		
		SwaggerConverter swaggerConverter = new SwaggerConverter();
		SwaggerParseResult swaggerParseResult = swaggerConverter.convert(swaggerDeserializationResult);
		OpenAPI openApi = swaggerParseResult.getOpenAPI();
		
		if (arguments.get(CommonConsts.CLI_OUTPUT_FORMAT).equals(CommonConsts.OUTPUT_FORMAT_JSON)) {
			return Json.pretty(openApi);
		} else {
			return Yaml.pretty().writeValueAsString(openApi);
		}
	}
	
	/**
	 * Determines the correct file name. First it is checked whether an argument was passed.
	 * If so, this argument will be used. If not, the file name from the code will be used. 
	 * If the code does not have a file name. The default file name will be used.
	 * The order is as follows:
	 * 	1) Argument
	 * 	2) Code
	 * 	3) Default
	 * 
	 * @param filenameFromCode The file name of the code if it exists or the default file name.
	 * @return A valid file name.
	 */
	protected String getFileName(String filenameFromCode) {
		boolean isArgumentValid = arguments.get(CommonConsts.CLI_FILE_NAME) != null && !arguments.get(CommonConsts.CLI_FILE_NAME).isEmpty();
		return isArgumentValid ? arguments.get(CommonConsts.CLI_FILE_NAME) : filenameFromCode;
 	}
}
