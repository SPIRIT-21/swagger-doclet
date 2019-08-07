package com.spirit21.spring.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.spirit21.common.exception.ApiParserException;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.common.parser.AbstractParser;
import com.spirit21.spring.helper.ParserHelper;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

/**
 * Root parser for Spring Boot projects.
 * It calls every single parser and afterwards create the file.
 * 
 * @author mweidmann
 */
public class Parser extends AbstractParser {

	public static final List<ClassDoc> CONTROLLER_CLASS_DOCS = new ArrayList<>();
	
	private TagParser tagParser;
	private PathParser pathParser;

	public Parser(RootDoc rootDoc, Map<String, String> arguments) {
		super(rootDoc, arguments);

		this.tagParser = new TagParser();
		this.pathParser = new PathParser();
	}
	
	@Override
	public boolean run() throws ApiParserException, IOException {
		try {
			searchEntryPointClassDoc(classDoc -> CommonHelper.hasAnnotation(classDoc, SpringBootApplication.class.getName()));
			searchControllerClassDocs();
			
			apiParser.run(swagger);
			tagParser.run(swagger);
			pathParser.run(swagger);
			definitionParser.run(swagger);
			
			String fileName = getFileName(apiParser.getFileName());
			writeFile(fileName);
			
			return true;
		} catch (ApiParserException e) {
			throw new ApiParserException("Error while parsing or searching the API entry point!", e);
		} catch (IOException e) {
			throw new IOException("Error while creating or writing the swagger file!", e);
		}
	}

	/**
	 * Finds all controller ClassDocs by checking if they are annotated with @Controller or @RestController.
	 * These ClassDocs are added to the ControllerClassDocs list.
	 */
	private void searchControllerClassDocs() {
		Arrays.asList(rootDoc.classes()).stream()
			.filter(ParserHelper::isController)
			.forEach(CONTROLLER_CLASS_DOCS::add);
	}
}
