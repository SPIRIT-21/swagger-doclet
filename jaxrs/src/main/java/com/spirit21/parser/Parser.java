package com.spirit21.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.spirit21.Consts;
import com.spirit21.exception.ApiParserException;
import com.spirit21.helper.ClassDocCache;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;

public class Parser {

	private RootDoc rootDoc;
	
	public static List<ClassDoc> definitionClassDocs;
	public static Map<ClassDoc, ClassDoc> resourceClassDocs;
	public static ClassDocCache classDocCache;
	protected static ClassDoc entryPointClassDoc;

	private ApiParser apiParser;
	private TagParser tagParser;
	private PathParser pathParser;
	private DefinitionParser definitionParser;

	private Swagger swagger;
	private String outputType;

	/**
	 * Initialize
	 */
	public Parser(RootDoc rootDoc, String outputType) {
		this.rootDoc = rootDoc;
		
		definitionClassDocs = new ArrayList<>();
		classDocCache = new ClassDocCache(Arrays.asList(rootDoc.classes()));
		
		apiParser = new ApiParser();
		tagParser = new TagParser();
		pathParser = new PathParser();
		definitionParser = new DefinitionParser();
		
		swagger = new Swagger();
		if (outputType == null) {
			this.outputType = "";
		} else {
			this.outputType = outputType;
		}
	}

	/**
	 * This method runs all the parsers and generates finally a swagger file
	 */
	public boolean run() throws ApiParserException, IOException {
		try {
			entryPointClassDoc = getEntryPointClassDoc();
			resourceClassDocs = getResources();

			apiParser.setBasicInformation(swagger);
			tagParser.setTags(swagger);
			pathParser.setPath(swagger);
			definitionParser.setDefinitions(swagger);

			writeFile(apiParser.getFileName());

			return true;
		} catch (ApiParserException e) {
			throw new ApiParserException("Error while parsing or searching API entry point!", e);
		} catch (IOException e) {
			throw new IOException("Error while creating or writing .json swagger file!", e);
		}
	}

	/**
	 * This method gets the entryPointClassDoc of the REST API and throws possibly
	 * an exception
	 */
	private ClassDoc getEntryPointClassDoc() throws ApiParserException {
		List<ClassDoc> tempList = Arrays.asList(rootDoc.classes()).stream()
				.filter(ParserHelper::hasApplicationPathAnnotation)
				.collect(Collectors.toList());
		if (tempList.size() == 1) {
			return tempList.get(0);
		} else if (tempList.size() > 1) {
			throw new ApiParserException("Multiple API entry points found! Only one entry point is allowed.");
		} else {
			throw new ApiParserException("Your API does not have any entry point!");
		}
	}

	/**
	 * This method puts all resources in a map and afterwards all subResources NOTE:
	 * Can't do it like: collect(Collectors.toMap(c -> c, null)) because it throws a
	 * NullPointerException if the value is null NOTE: Create temporary hashMap
	 * because during iteration you cannot put something in the map
	 */
	private Map<ClassDoc, ClassDoc> getResources() {
		LinkedHashMap<ClassDoc, ClassDoc> tempMap = Arrays.asList(rootDoc.classes()).stream()
				.filter(ParserHelper::hasPathAnnotation)
				.collect(LinkedHashMap::new, (m, c) -> m.put(c, null), HashMap::putAll);

		LinkedHashMap<ClassDoc, ClassDoc> hashMap = new LinkedHashMap<>(tempMap);
		hashMap.entrySet().forEach(c -> getSubs(tempMap, c.getKey()));
		return tempMap;
	}

	/**
	 * This method gets all subResources and its subSubResources... (because of
	 * recursion)
	 */
	private void getSubs(LinkedHashMap<ClassDoc, ClassDoc> tempMap, ClassDoc classDoc) {
		Arrays.asList(classDoc.methods()).stream()
			.filter(m -> !ParserHelper.hasHttpMethod(m))
			.filter(ParserHelper::hasPathAnnotation)
			.map(m -> classDocCache.findByType(m.returnType()))
			.filter(Objects::nonNull)
			.forEach(c -> {
					tempMap.put(c, classDoc);
					getSubs(tempMap, c);
				});
	}

	/**
	 * This method creates a new .json file and possibly throws an exception if
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
	 * This method writes in a file (json or yaml) and possibly throws and exception if
	 * something failed
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