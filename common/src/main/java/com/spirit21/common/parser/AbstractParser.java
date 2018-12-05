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
import com.spirit21.common.Consts;
import com.spirit21.common.exception.ApiParserException;
import com.spirit21.common.helper.ClassDocCache;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

import io.swagger.oas.models.OpenAPI;
import io.swagger.parser.models.SwaggerParseResult;
import io.swagger.parser.v2.SwaggerConverter;
import v2.io.swagger.models.Swagger;
import v2.io.swagger.parser.util.SwaggerDeserializationResult;
import v2.io.swagger.util.Json;
import v2.io.swagger.util.Yaml;

public abstract class AbstractParser {
	
	protected RootDoc rootDoc;
	
	public static List<ClassDoc> definitionClassDocs;
	public static ClassDocCache classDocCache;
	protected static ClassDoc entryPointClassDoc;
	public static Pattern pattern;
	
	protected ApiParser apiParser;
	protected DefinitionParser definitionParser;
	
	protected Swagger swagger;
	private Map<String, String> arguments;
	
	/**
	 * Initialize
	 */
	public AbstractParser(RootDoc rootDoc, Map<String, String> arguments) {
		this.rootDoc = rootDoc;
		this.arguments = arguments;
		
		definitionClassDocs = new ArrayList<>();
		classDocCache = new ClassDocCache(Arrays.asList(rootDoc.classes()));
		
		this.apiParser = new ApiParser();
		this.definitionParser = new DefinitionParser();
		
		this.swagger = new Swagger();
		
		pattern = Pattern.compile("\"?/?([a-zA-Z0-9_-]+)/?.*\"?");
	}
	
	public abstract boolean run() throws ApiParserException, IOException;
	
	/**
	 * This method gets the classDoc of the entry point of the REST API
	 * and throws possibly an exception if there are some issues
	 */
	protected ClassDoc getEntryPointClassDoc(Predicate<? super ClassDoc> predicate) throws ApiParserException {
		List<ClassDoc> tempList = Arrays.asList(rootDoc.classes()).stream()
				.filter(predicate)
				.collect(Collectors.toList());
		
		if (tempList.size() == 1) {
			return tempList.get(0);
		} else if (tempList.size() > 1) {
			throw new ApiParserException("Mutliple API entry points found! Only one entry point is allowed.");
		} else {
			throw new ApiParserException("Your API does not have any entry point!");
		}
	}
	
	/**
	 * This method creates a new file and possibly throws an exception if
	 * something failed
	 */
	private File createFile(String fileName) throws IOException {
		File file = new File(fileName + "." + arguments.get(Consts.OUTPUT_TYPE));
		
		if (file.createNewFile()) {
			return file;
		} else {
			throw new IOException("Could not create a swagger file!");
		}
	}

	/**
	 * This method writes in a file (json or yaml) and possibly throws an exception
	 * if something went wrong
	 */
	protected void writeFile(String fileName) throws IOException {
		String output = null;
		
		if (arguments.get(Consts.VERSION).equals(Consts.VERSION_3)) {
			output = getSwaggerV3();
		} else {
			output = getSwaggerV2();
		}
		
		File file = createFile(fileName);

		try (FileWriter fw = new FileWriter(file)) {
			fw.write(output);
		} catch (IOException e) {
			throw new IOException("Could not write in the swagger file!", e);
		}
	}
	
	/**
	 * This method returns the swagger string for version 2
	 */
	private String getSwaggerV2() throws JsonProcessingException {
		String output = null;
		
		if (arguments.get(Consts.OUTPUT_TYPE).equals(Consts.JSON)) {
			output = Json.pretty(swagger);
		} else {
			output = Yaml.pretty().writeValueAsString(swagger);
		}
		return output;
	}
	
	/**
	 * This method returns the swagger string for version 3
	 */
	private String getSwaggerV3() throws JsonProcessingException {
		String output = null;
		
		SwaggerDeserializationResult swaggerDeserializationResult = new SwaggerDeserializationResult();
		
		swaggerDeserializationResult.setSwagger(swagger);
		
		SwaggerConverter converter = new SwaggerConverter();
		SwaggerParseResult result = converter.convert(swaggerDeserializationResult);
		OpenAPI openApi = result.getOpenAPI();
		
		if (arguments.get(Consts.OUTPUT_TYPE).equals(Consts.JSON)) {
			output = Json.pretty(openApi);
		} else {
			output = Yaml.pretty().writeValueAsString(openApi);
		}
		return output;
	}
	
	/**
	 * This method determines whether the file name of the arguments should be used 
	 * or the file name given in code
	 */
	protected String getFileName(String javadocFileName) {
		if (arguments.get(Consts.FILENAME) == null || arguments.get(Consts.FILENAME).isEmpty()) {
			return javadocFileName;
		} else {
			return arguments.get(Consts.FILENAME);
		}
 	}
}
