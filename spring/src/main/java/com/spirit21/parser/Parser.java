package com.spirit21.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.spirit21.common.Consts;
import com.spirit21.common.exception.ApiParserException;
import com.spirit21.common.helper.ClassDocCache;
import com.spirit21.common.parser.ApiParser;
import com.spirit21.common.parser.DefinitionParser;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;

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

	/**
	 * Inititalize
	 */
	public Parser(RootDoc rootDoc, String outputType) {
		this.rootDoc = rootDoc;
		this.outputType = outputType;

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
	 * This method finds all Controller ClassDoc's, saves them in a list and returns it
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
		if (fileName.isEmpty()) {
			fileName = Consts.STANDARD_FILE_NAME;
		}
		
		File file = new File(fileName + "." + outputType);
		
		if (file.createNewFile()) {
			return file;
		} else {
			throw new IOException("Could not create swagger file!");
		}
	}
	
	/**
	 * This method writes in a file (json or yaml) and possibly throws an exception
	 * if something failed
	 */
	private void writeFile(String fileName) throws IOException {
		String output;
		
		switch (outputType) {
		case "json":
			output = Json.pretty(swagger);
			break;
		case "yaml":
			output = Yaml.pretty().writeValueAsString(swagger);
			break;
		default:
			outputType = "json";
			output = Json.pretty(swagger);
		}
		
		File file = createFile(fileName);
		
		try (FileWriter fw = new FileWriter(file)) {
			fw.write(output);
		} catch (IOException e) {
			throw new IOException("Could not write in the swagger file!", e);
		}
	}
}