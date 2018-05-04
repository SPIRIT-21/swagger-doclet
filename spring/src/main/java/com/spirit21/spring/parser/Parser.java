package com.spirit21.spring.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.spirit21.common.Consts;
import com.spirit21.common.exception.ApiParserException;
import com.spirit21.common.helper.ClassDocCache;
import com.spirit21.common.parser.ApiParser;
import com.spirit21.common.parser.DefinitionParser;
import com.spirit21.spring.helper.ParserHelper;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

import io.swagger.oas.models.OpenAPI;
import io.swagger.parser.models.SwaggerParseResult;
import io.swagger.parser.v2.SwaggerConverter;
import v2.io.swagger.models.Swagger;
import v2.io.swagger.parser.util.SwaggerDeserializationResult;
import v2.io.swagger.util.Json;
import v2.io.swagger.util.Yaml;

public class Parser {

	private RootDoc rootDoc;

	public static List<ClassDoc> controllerClassDocs;
	
	public static List<ClassDoc> definitionClassDocs;
	public static ClassDocCache classDocCache;
	protected static ClassDoc entryPointClassDoc;
	protected static Pattern pattern;

	private ApiParser apiParser;
	private TagParser tagParser;
	private PathParser pathParser;
	private DefinitionParser definitionParser;

	private Swagger swagger;
	private String outputType;
	private String version;

	/**
	 * Inititalize
	 */
	public Parser(RootDoc rootDoc, String outputType, String version) {
		this.rootDoc = rootDoc;
		this.outputType = outputType;
		this.version = version;

		definitionClassDocs = new ArrayList<>();
		classDocCache = new ClassDocCache(Arrays.asList(rootDoc.classes()));

		apiParser = new ApiParser();
		tagParser = new TagParser();
		pathParser = new PathParser();
		definitionParser = new DefinitionParser();

		swagger = new Swagger();
		pattern = Pattern.compile("\"?/?([a-zA-Z0-9_-]+)/?.*\"?");
	}

	/**
	 * This method runs all parser and generates finally a swagger file
	 */
	public boolean run() throws ApiParserException, IOException {
		try {
			entryPointClassDoc = getEntryPointClassDoc();
			controllerClassDocs = getControllerClassDocs();
			
			apiParser.setBasicInformation(swagger, entryPointClassDoc);
			tagParser.setTags(swagger);
			pathParser.setPath(swagger);
			definitionParser.setDefinitions(swagger, definitionClassDocs, classDocCache);
			
			writeFile(apiParser.getFileName());
			
			return true;
		} catch (ApiParserException e) {
			throw new ApiParserException("Error while parsing or searching the API entry point!", e);
		} catch (IOException e) {
			throw new IOException("Error while creating or writing the swagger file!", e);
		}
	}

	/**
	 * This method gets the classDoc of the entry point of the Spring Boot REST API
	 * and throws possibly an exception if something failed.
	 */
	private ClassDoc getEntryPointClassDoc() throws ApiParserException {
		List<ClassDoc> tempList = Arrays.asList(rootDoc.classes()).stream()
				.filter(ParserHelper::hasSpringBootApplicationAnnotation)
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
	 * This method finds all controller classDocs, saves them in a list and returns it
	 */
	private List<ClassDoc> getControllerClassDocs() {
		return Arrays.asList(rootDoc.classes()).stream()
				.filter(ParserHelper::hasControllerAnnotation)
				.collect(Collectors.toList());
	}
	
	/**
	 * This method creates a new file and possibly throws an exception if 
	 * something failed
	 */
	private File createFile(String fileName) throws IOException {
		File file = new File(fileName + "." + outputType);
		
		if (file.createNewFile()) {
			return file;
		} else {
			throw new IOException("Could not create the swagger file!");
		}
	}
	
	/**
	 * This method writes in a file (json or yaml) and possibly throws an exception
	 * if something failed
	 */
	private void writeFile(String fileName) throws IOException {
		String output = null;
		
		if (version.equals(Consts.VERSION_3)) {
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
		
		if (outputType.equals(Consts.JSON)) {
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
		
		if (outputType.equals(Consts.JSON)) {
			output = Json.pretty(openApi);
		} else {
			output = Yaml.pretty().writeValueAsString(openApi);
		}
		return output;
	}
}
