package com.spirit21.spring.parser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.spirit21.common.exception.ApiParserException;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.common.parser.AbstractParser;
import com.spirit21.spring.helper.ParserHelper;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

public class Parser extends AbstractParser {

	public static List<ClassDoc> controllerClassDocs;
	
	private TagParser tagParser;
	private PathParser pathParser;

	/**
	 * Initialize
	 */
	public Parser(RootDoc rootDoc, Map<String, String> arguments) {
		super(rootDoc, arguments);

		this.tagParser = new TagParser();
		this.pathParser = new PathParser();
	}
	
	/**
	 * This method runs all parser and generates at the end a swagger file
	 */
	@Override
	public boolean run() throws ApiParserException, IOException {
		try {
			entryPointClassDoc = getEntryPointClassDoc(classDoc -> CommonHelper.hasAnnotation(classDoc, SpringBootApplication.class.getName()));
			controllerClassDocs = getControllerClassDocs();
			
			apiParser.run(swagger);
			tagParser.setTags(swagger);
			pathParser.setPath(swagger);
			definitionParser.run(swagger);
			
			writeFile(getFileName(apiParser.getFileName()));
			
			return true;
		} catch (ApiParserException e) {
			throw new ApiParserException("Error while parsing or searching the API entry point!", e);
		} catch (IOException e) {
			throw new IOException("Error while creating or writing the swagger file!", e);
		}
	}

	/**
	 * This method finds all controller classDocs, saves them in a list and returns it
	 */
	private List<ClassDoc> getControllerClassDocs() {
		return Arrays.asList(rootDoc.classes()).stream()
				.filter(ParserHelper::isController)
				.collect(Collectors.toList());
	}
}
