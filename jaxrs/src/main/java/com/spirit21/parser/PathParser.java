package com.spirit21.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.PathParam;

import com.spirit21.handler.annotation.HttpMethodHandler;
import com.spirit21.handler.parameter.PathParameterHandler;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;

public class PathParser {

	private OperationParser operationParser;
	private PathParameterHandler pathParameter;
	
	/**
	 * Initialize
	 */
	protected PathParser() {
		operationParser = new OperationParser();
		pathParameter = new PathParameterHandler(PathParam.class.getName());
	}
	
	/**
	 * This method creates a 'paths' map, puts data in there and adds it to the swagger model
	 */
	protected void setPath(Swagger swagger) {
		Map<String, Path> paths = new LinkedHashMap<>();
		paths.putAll(getPaths(swagger));
		swagger.setPaths(paths);
	}
	
	/**
	 * This method iterates over all resources and checks if the resource has HttpMethods
	 * If so, then it creates a path object
	 */
	private Map<String, Path> getPaths(Swagger swagger) {
		Map<String, Path> tempPaths = new LinkedHashMap<>();
		
		Parser.resourceClassDocs.entrySet().stream()
			.filter(entry -> ParserHelper.isResource(entry.getKey()))
			.forEach(entry -> createPath(entry.getKey(), tempPaths));
		
		return tempPaths;
	}
	
	/**
	 * This method creates a path for a single resource, sets its path parameters, creates the operations
	 * and put it in the map
	 */
	private void createPath(ClassDoc classDoc, Map<String, Path> tempPaths) {
		Path path = new Path();
		
		path.setParameters(new ArrayList<>(getPathParameters(classDoc)));
		createOperation(path, classDoc);
		
		tempPaths.put(ParserHelper.getPath(classDoc), path);
	}
	
	/**
	 * This method gets all parameters from a resource and its parent resources.
	 */
	private Set<Parameter> getPathParameters(ClassDoc classDoc) {
		Set<Parameter> parameters = new HashSet<>();

		parameters.addAll(getPathParameterFromField(classDoc));
		
		ClassDoc parent = Parser.resourceClassDocs.get(classDoc);
		
		if (parent != null) {
			Arrays.asList(parent.methods()).stream()
				.filter(ParserHelper::hasPathAnnotation)
				.filter(methodDoc -> !ParserHelper.isHttpMethod(methodDoc))
				.filter(methodDoc -> methodDoc.returnType().equals(classDoc))
				.map(this::getPathParameterFromMethodParameter)
				.flatMap(List::stream)
				.forEach(parameters::add);
			
			parameters.addAll(getPathParameters(parent));
			return parameters;
		} else {
			return parameters;
		}
	}
	
	/**
	 * This method gets the path parameter from the fields of a ClassDoc
	 */
	private List<Parameter> getPathParameterFromField(ClassDoc classDoc) {
		return Arrays.asList(classDoc.fields(false)).stream()
			.filter(f -> ParserHelper.hasAnnotation(f, pathParameter.getName()))
			.map(f -> pathParameter.createPathParameterFromField(f))
			.collect(Collectors.toList());
	}
	
	/**
	 * This method gets the path parameter from the parameter of a method
	 */
	private List<Parameter> getPathParameterFromMethodParameter(MethodDoc methodDoc) {
		return Arrays.asList(methodDoc.parameters()).stream()
			.filter(p -> ParserHelper.hasAnnotation(p, pathParameter.getName()))
			.map(p -> pathParameter.createNewParameter(p, methodDoc))
			.collect(Collectors.toList());
	}
	
	/**
	 * This method creates the operations to our path
	 */
	// TODO: Methods with HTTP AND Path annotation
	private void createOperation(Path path, ClassDoc classDoc) {
		Arrays.asList(classDoc.methods()).stream()
			.filter(ParserHelper::isHttpMethod)
			.filter(methodDoc -> !ParserHelper.hasPathAnnotation(methodDoc))
			.forEach(methodDoc -> {
				Operation operation = operationParser.createOperation(classDoc, methodDoc);
				setOperationToPath(path, operation, methodDoc);
			});
	}
	
	/**
	 * This method sets an operation to the path
	 */
	private void setOperationToPath(Path path, Operation operation, MethodDoc methodDoc) {
		Arrays.asList(HttpMethodHandler.values()).stream()
			.filter(hmh -> hmh.getFullName().equals(ParserHelper.getFullHttpMethod(methodDoc)))
			.forEach(hmh -> hmh.setOperationToPath(path, operation));
	}
}