package com.spirit21.parser;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

import io.swagger.models.Swagger;

public class Parser {

	public static List<ClassDoc> controllerClassDocs;
	public static List<ClassDoc> entityClassDocs;
	protected static ClassDoc entryPointClassDoc;

	private RootDoc rootDoc;
	private ApiParser apiParser;
	private TagParser tagParser;
	private PathParser pathParser;
	private DefinitionParser definitionParser;

	private Swagger swagger;

	public Parser(RootDoc rootDoc) {
		this.rootDoc = rootDoc;
		swagger = new Swagger();
		apiParser = new ApiParser();
		tagParser = new TagParser();
		pathParser = new PathParser();
		definitionParser = new DefinitionParser();
		entityClassDocs = new ArrayList<>();
	}

	public boolean run() {
		try {
			entryPointClassDoc = getEntryPointClassDoc();
			controllerClassDocs = getControllerClassDocs();

			apiParser.setBasicInformation(swagger);
			tagParser.setTags(swagger);
			pathParser.setPath(swagger);

			// definitionParser
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private ClassDoc getEntryPointClassDoc() {
		List<ClassDoc> tempList = new ArrayList<>();
		for (ClassDoc classDoc : rootDoc.classes()) {
			if (ParserHelper.hasAnnotation(classDoc, SpringBootApplication.class.getName())) {
				tempList.add(classDoc);
			}
		}
		if (tempList.size() == 1) {
			return tempList.get(0);
		} else if (tempList.size() > 1) {
			// EXCEPTION
			return null;
		} else {
			// EXCEPTION
			return null;
		}
	}

	private List<ClassDoc> getControllerClassDocs() {
		List<ClassDoc> tempList = new ArrayList<>();
		for (ClassDoc classDoc : rootDoc.classes()) {
			if (ParserHelper.hasAnnotation(classDoc, RestController.class.getName())
					|| (ParserHelper.hasAnnotation(classDoc, Controller.class.getName())
							&& ParserHelper.hasAnnotation(classDoc, ResponseBody.class.getName()))) {
				tempList.add(classDoc);
			}
		}
		return tempList;
	}
}