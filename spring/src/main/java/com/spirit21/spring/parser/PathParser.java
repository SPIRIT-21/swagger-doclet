package com.spirit21.spring.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PathVariable;

import com.spirit21.common.CommonConsts;
import com.spirit21.common.helper.AnnotationHelper;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.spring.handler.annotation.HttpMethodHandler;
import com.spirit21.spring.handler.parameter.PathParameterHandler;
import com.spirit21.spring.helper.ParserHelper;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.PathParameter;

public class PathParser {

	private PathParameterHandler pathParameterHandler;
	private OperationParser operationParser;
	private Pattern pattern;
	
	private Map<String, Path> paths;
	private Map<String, List<MethodDoc>> mappingAndMethods;

	/**
	 * Initialize
	 */
	protected PathParser() {
		this.paths = new LinkedHashMap<>();
		this.mappingAndMethods = new LinkedHashMap<>();
		
		this.operationParser = new OperationParser();
		this.pathParameterHandler = new PathParameterHandler(PathVariable.class.getName());
		
		this.pattern = Pattern.compile("(?<=\\{)([a-zA-Z0-9_-]+)(?=\\})");
	}

	/**
	 * This method creates for every mapping path a path model for the swagger model
	 */
	protected void setPath(Swagger swagger) {
		Parser.controllerClassDocs.forEach(this::separateClassDocs);
		createPath();
		swagger.setPaths(paths);
	}
	
	/**
	 * This method separates every class doc to the path and a list of methods handling the
	 * requests for this path
	 */
	private void separateClassDocs(ClassDoc classDoc) {
		for (MethodDoc methodDoc : classDoc.methods()) {
			List<String> controllerMappings = getControllerMappings(classDoc);
			
			if (controllerMappings.isEmpty()) controllerMappings.add("/");
			
			for (String controllerMapping : controllerMappings) {
				if (ParserHelper.getMappingValue(methodDoc).length == 0) {
					putInMapppingAndMethods(controllerMapping, methodDoc);
				}
				
				List<String> methodMappings = ParserHelper.getAllFullMappingsOfMethod(controllerMapping, methodDoc);
				methodMappings.forEach(methodMapping -> putInMapppingAndMethods(methodMapping, methodDoc));
			}
		}
	}
	
	/**
	 * This method creates the path object puts it in the map to the swagger model
	 */
	private void createPath() {
		for (Entry<String, List<MethodDoc>> entry : mappingAndMethods.entrySet()) {
			Path path = new Path();
			
			getPathParameters(entry.getKey(), entry.getValue()).forEach(path::addParameter);
			createOperation(entry.getKey(), entry.getValue(), path);
			
			paths.put(entry.getKey(), path);
		}
	}

	/**
	 * This method creates a collection of parameters and returns it
	 */
	private Set<PathParameter> getPathParameters(String pathName, List<MethodDoc> methodDocs) {
		Set<String> pathParameterNames = getPathParameterNames(pathName);
		
		return methodDocs.stream()
			.filter(ParserHelper::isHttpMethod)
			.map(methodDoc -> checkAndCreateParameter(pathParameterNames, methodDoc))
			.flatMap(Set::stream)
			.collect(Collectors.toSet());
	}
	
	/**
	 * This method creates the operation object for our path
	 */
	private void createOperation(String pathName, List<MethodDoc> methodDocs, Path path) {
		for (MethodDoc methodDoc : methodDocs) {
			Map<String, Operation> operations = operationParser.createOperations(methodDoc, pathName);
			
			operations.entrySet()
				.forEach(entry -> setOperationToPath(path, entry.getValue(), entry.getKey()));
		}
	}
	
	/**
	 * This method sets an operation object to a path object
	 */
	private void setOperationToPath(Path path, Operation operation, String httpMethod) {
		Arrays.asList(HttpMethodHandler.values()).stream()
			.filter(hmh -> hmh.getSimpleHttpMethodName().equals(httpMethod))
			.forEach(hmh -> hmh.setOperationToPath(operation, path));
	}
	
	/**
	 * This method gets the mapping of a controller
	 */
	private List<String> getControllerMappings(ClassDoc classDoc) {
		AnnotationValue[] aValues = ParserHelper.getMappingValue(classDoc);
		return Arrays.asList(aValues).stream()
				.map(aValue -> (String) CommonHelper.getAnnotationValueObject(aValue))
				.collect(Collectors.toList());
	}
	
	/**
	 * This method puts the mapping value and the methodDoc in the map
	 */
	private void putInMapppingAndMethods(String mappingValue, MethodDoc methodDoc) {
		if (mappingAndMethods.containsKey(mappingValue)) {
			mappingAndMethods.get(mappingValue).add(methodDoc);
		} else {
			List<MethodDoc> list = new ArrayList<>();
			list.add(methodDoc);
			mappingAndMethods.put(mappingValue, list);
		}
	}
	
	/**
	 * This method checks the parameters of a method and creates eventually a swagger parameter
	 */
	private Set<PathParameter> checkAndCreateParameter(Set<String> pathParamNames, MethodDoc methodDoc) {
		return Arrays.asList(methodDoc.parameters()).stream()
			.filter(param -> CommonHelper.hasAnnotation(param, pathParameterHandler.getHttpParameterType()))
			.filter(param -> checkPathParamName(pathParamNames, param))
			.map(param -> pathParameterHandler.createNewSwaggerParameter(methodDoc, param))
			.collect(Collectors.toSet());
	}
	
	/**
	 * This method gets all path parameter names from the mapping value of a classDoc
	 */
	private Set<String> getPathParameterNames(String path) {
		Set<String> pathParameterNames = new HashSet<>();

		Matcher matcher = pattern.matcher(path);
		while (matcher.find()) {
			pathParameterNames.add(matcher.group());
		}
		return pathParameterNames;
	}

	/**
	 * This method checks if one of the strings has the same name as the parameter
	 * or the PathVariable annotation
	 */
	private boolean checkPathParamName(Set<String> pathParamNames, com.sun.javadoc.Parameter param) {
		AnnotationHelper annotationHelper = new AnnotationHelper(param.annotations());

		String value = (String) ParserHelper.getAnnotationValueOfTwoSpecifics(annotationHelper,
				pathParameterHandler.getHttpParameterType(), com.spirit21.spring.Consts.NAME, CommonConsts.ANNOTATION_PROPERTY_NAME_VALUE);

		if (pathParamNames.contains(value)) {
			pathParamNames.remove(value);
			return true;
		} else if (pathParamNames.contains(param.name())) {
			pathParamNames.remove(param.name());
			return true;
		}
		return false;
	}
}
