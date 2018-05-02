package com.spirit21.parser;

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

import com.spirit21.common.Consts;
import com.spirit21.common.helper.AnnotationHelper;
import com.spirit21.handler.annotation.HttpMethodHandler;
import com.spirit21.handler.parameter.PathParameterHandler;
import com.spirit21.helper.ParserHelper;
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
		paths = new LinkedHashMap<>();
		mappingAndMethods = new LinkedHashMap<>();
		
		operationParser = new OperationParser();
		pathParameterHandler = new PathParameterHandler(PathVariable.class.getName());
		
		pattern = Pattern.compile("(?<=\\{)([a-zA-Z0-9_-]+)(?=\\})");
	}

	/**
	 * This method creates for every mapping path a path model for the swagger model
	 */
	protected void setPath(Swagger swagger) {
		Parser.controllerClassDocs.forEach(this::seperateClassDocs);
		createPath();
		swagger.setPaths(paths);
	}
	
	
	/**
	 * This method separates every class doc to the path and a list of methods handling the
	 * request for this path
	 */
	private void seperateClassDocs(ClassDoc classDoc) {
		for (MethodDoc methodDoc : classDoc.methods()) {
			List<String> controllerMappings = getControllerMappings(classDoc);
			
			if (controllerMappings.isEmpty()) controllerMappings.add("/");
			
			for (String controllerMapping : controllerMappings) {
				if (ParserHelper.getMappingValue(methodDoc).length == 0) {
					putInMapppingAndMethods(controllerMapping, methodDoc);
				}
				
				String[] methodMappings = ParserHelper.getPath(controllerMapping, methodDoc);
				Arrays.asList(methodMappings).forEach(methodMapping -> putInMapppingAndMethods(methodMapping, methodDoc));
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
		methodDocs.stream()
			.filter(ParserHelper::isHttpMethod)
			.forEach(methodDoc -> {
				Map<String, Operation> operations = operationParser.createOperations(methodDoc, pathName);
				operations.entrySet().forEach(e -> setOperationToPath(path, e.getValue(), e.getKey()));
			});
	}
	
	/**
	 * This method sets an operation object to a path object
	 */
	private void setOperationToPath(Path path, Operation operation, String httpMethod) {
		Arrays.asList(HttpMethodHandler.values()).stream()
			.filter(hmh -> hmh.getSimpleName().equals(httpMethod))
			.forEach(hmh -> hmh.setOperationToPath(path, operation));
	}
	
	/**
	 * This method gets the mapping of a controller
	 */
	private List<String> getControllerMappings(ClassDoc classDoc) {
		AnnotationValue[] aValues = ParserHelper.getMappingValue(classDoc);
		return Arrays.asList(aValues).stream()
				.map(aValue -> (String) ParserHelper.getAnnotationValueObject(aValue))
				.collect(Collectors.toList());
	}
	
	/**
	 * This method puts the mapping value and its method in the map
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
			.filter(param -> ParserHelper.hasAnnotation(param, pathParameterHandler.getName()))
			.filter(param -> checkPathParamName(pathParamNames, param))
			.map(param -> pathParameterHandler.createNewParameter(param, methodDoc))
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
				pathParameterHandler.getName(), com.spirit21.Consts.NAME, Consts.VALUE);

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
