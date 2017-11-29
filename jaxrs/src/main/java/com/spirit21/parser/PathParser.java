package com.spirit21.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	 * This method creates a 'paths' map, puts data in there and returns it
	 */
	protected void setPath(Swagger swagger) {
		Map<String, Path> paths = new LinkedHashMap<>();
		paths.putAll(getPaths(swagger));
		swagger.setPaths(paths);
	}
	
	/**
	 * This method iterates over all resources and checks if the resource has HttpMethods
	 * If yes, then it calls the method createPath
	 */
	private Map<String, Path> getPaths(Swagger swagger) {
		Map<String, Path> tempPaths = new LinkedHashMap<>();
		Parser.resourceClassDocs.entrySet().stream()
			.filter(e -> ParserHelper.isResource(e.getKey()))
			.forEach(e -> createPath(swagger, e.getKey(), tempPaths));
		return tempPaths;
	}
	
	/**
	 * This method creates a Path for a single resource, sets its PathParameters, creates the operations
	 * and put it in the map
	 */
	private void createPath(Swagger swagger, ClassDoc classDoc, Map<String, Path> tempPaths) {
		Path path = new Path();
		path.setParameters(new ArrayList<>(getPathParameters(classDoc)));
		createOperation(swagger, path, classDoc);
		tempPaths.put(ParserHelper.getPath(classDoc), path);
	}
	
	/**
	 * This method gets all parameters from a resource and its parent resources.
	 */
	private Set<Parameter> getPathParameters(ClassDoc classDoc) {
		Set<Parameter> parameters = new HashSet<>();

		// Adds all path parameters of the current ClassDoc from fields
		parameters.addAll(getPathParameterFromField(classDoc));
		
		// Gets the current parentClassDoc of the classDoc
		ClassDoc parent = Parser.resourceClassDocs.get(classDoc);
		// Check if the parent is a top-level-resource 
		if (parent != null) {
			// Iterate through methods of the parent ClassDoc and filter the method which returns the classDoc
			Arrays.asList(parent.methods()).stream()
				.filter(ParserHelper::hasPathAnnotation)
				.filter(m -> !ParserHelper.hasHttpMethod(m) && m.returnType().equals(classDoc))
				// Add to parameters list all pathParameter from the parameter of the method
				.forEach(m -> parameters.addAll(getPathParameterFromMethodParameter(m)));
			// Get the pathParameter of the parentClassDoc --> recursion
			parameters.addAll(getPathParameters(parent));
			return parameters;
		} else {
			return parameters;
		}
	}
	
	/**
	 * This method gets the pathParameter from the parameter of a method
	 */
	private List<Parameter> getPathParameterFromMethodParameter(MethodDoc methodDoc) {
		List<Parameter> parameters = new ArrayList<>();
		
		Arrays.asList(methodDoc.parameters()).stream()
			.filter(p -> ParserHelper.hasAnnotation(p, pathParameter.getName()))
			.map(p -> pathParameter.createNewParameter(p, methodDoc))
			.forEach(parameters::add);
		return parameters;
	}
	
	/**
	 * This method gets the pathParameter from the fields of a ClassDoc
	 */
	private List <Parameter> getPathParameterFromField(ClassDoc classDoc) {
		List <Parameter> parameters = new ArrayList<>();
		
		// Iterate over fields and filter fields with @PathParam annotation
		Arrays.asList(classDoc.fields(false)).stream()
			.filter(f -> ParserHelper.hasAnnotation(f, pathParameter.getName()))
			.map(f -> pathParameter.createPathParameterFromField(f))
			.forEach(parameters::add);
		return parameters;
	}
	
	/**
	 * This method creates the operations to our path
	 */
	private void createOperation(Swagger swagger, Path path, ClassDoc classDoc) {
		// Iterate over the methods
		Arrays.asList(classDoc.methods()).stream()
			// Filter the methods which has httpMethodAnnotations but no Path annotation
			.filter(ParserHelper::hasHttpMethod)
			.filter(m -> !ParserHelper.hasPathAnnotation(m))
			// For every method it creates an operation and sets the operation into the path 
			.forEach(m -> {
				Operation operation = operationParser.createOperation(swagger, classDoc, m);
				setOperationToPath(path, operation, m);
				});
	}
	
	/**
	 * This method sets an operation to the path
	 */
	private void setOperationToPath(Path path, Operation operation, MethodDoc methodDoc) {
		// Iterate over HttpMethodHandler
		Arrays.asList(HttpMethodHandler.values()).stream()
			// Check the httpMethod of the method
			.filter(hmh -> ParserHelper.getFullHttpMethod(methodDoc).equals(hmh.getFullName()))
			// sets the operation to path
			.forEach(hmh -> hmh.setOperationToPath(path, operation));
	}
}