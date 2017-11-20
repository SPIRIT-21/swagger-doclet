package com.spirit21.parser;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;

import com.spirit21.handler.annotation.HttpMethodHandler;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;

public class PathParser {

	private OperationParser operationParser;

	protected PathParser() {
		operationParser = new OperationParser();
	}

	protected void setPath(Swagger swagger) {
		Map<String, Path> paths = new LinkedHashMap<>();
		paths.putAll(setControllerPaths(swagger));
		swagger.setPaths(paths);
	}

	private Map<String, Path> setControllerPaths(Swagger swagger) {
		Map<String, Path> tempPaths = new LinkedHashMap<>();
		for (ClassDoc classDoc : Parser.controllerClassDocs) {
			if (ParserHelper.checkController(classDoc)) {
				String s = ParserHelper.getPathOrValueOfAnnotation(classDoc, RequestMapping.class.getName());
				if (s != null) {
					Path path = new Path();
					createOperation(path, classDoc);
					tempPaths.put(s, path);
				} else {
					for (MethodDoc methodDoc : classDoc.methods()) {
						String st = ParserHelper.getFullPath(methodDoc);
						for (Tag tag : swagger.getTags()) {
							if (tag.getName().equals(st)) {
								tempPaths.get(st);
								ParserHelper.getHttpMethod(methodDoc).annotationType();
							}
						}
					}
				}
			}
		}
		return tempPaths;
	}

	private void createOperation(Path path, ClassDoc classDoc) {
		for (MethodDoc methodDoc : classDoc.methods()) {
			if (ParserHelper.hasHttpMethod(methodDoc)) {
				Operation operation = operationParser.createOperation(methodDoc);
				for (HttpMethodHandler hmh : HttpMethodHandler.values()) {
					AnnotationDesc annotation = ParserHelper.getHttpMethod(methodDoc);
					if (annotation.annotationType().qualifiedTypeName().equals(hmh.getFullName())) {
						hmh.setOperationToPath(path, operation);
					}
				}
			}
		}
	}
}