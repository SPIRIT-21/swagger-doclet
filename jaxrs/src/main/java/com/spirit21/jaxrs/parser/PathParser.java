package com.spirit21.jaxrs.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.ws.rs.PathParam;

import com.spirit21.common.CommonConsts;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.jaxrs.handler.annotation.HttpMethodHandler;
import com.spirit21.jaxrs.handler.parameter.PathParameterHandler;
import com.spirit21.jaxrs.helper.ParserHelper;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;

public class PathParser {

	private PathParameterHandler pathParameterHandler;
	private OperationParser operationParser;
	
	private Map<String, Path> paths;
	private Map<String, List<MethodDoc>> mappingAndMethods;
	
	/**
	 * Initialize
	 */
	protected PathParser() {
		this.paths = new LinkedHashMap<>();
		this.mappingAndMethods = new LinkedHashMap<>();
		
		this.operationParser = new OperationParser();
		this.pathParameterHandler = new PathParameterHandler(PathParam.class.getName());
	}
	
	/**
	 * This method creates separates the classDocs, create the path models and sets it to the swagger model
	 */
	protected void setPath(Swagger swagger) {
		Parser.resourceClassDocs.entrySet().stream()
			.map(Entry::getKey)
			.filter(ParserHelper::isResource)
			.forEach(this::separateClassDocs);
		createPath();
		swagger.setPaths(paths);
	}
	
	/**
	 * This method separates every classDoc to the path and a list of methods handling the
	 * requests for this path
	 */
	private void separateClassDocs(ClassDoc classDoc) {
		for (MethodDoc methodDoc : classDoc.methods()) {
			String classDocPath = ParserHelper.getPath(classDoc);
			
			String pathAnnotation = javax.ws.rs.Path.class.getName();
			
			if (CommonHelper.hasAnnotation(methodDoc, pathAnnotation) && ParserHelper.isHttpMethod(methodDoc)) {
				AnnotationValue aValue = CommonHelper.getAnnotationValue(methodDoc, pathAnnotation, CommonConsts.ANNOTATION_PROPERTY_NAME_VALUE);
				String methodPath = (String) CommonHelper.getAnnotationValueObject(aValue);
			
				String mappingValue = CommonHelper.replaceMultipleSlashes(classDocPath + "/" + methodPath);
				
				putInMappingAndMethods(mappingValue, methodDoc);
			} else if (!CommonHelper.hasAnnotation(methodDoc, pathAnnotation) && ParserHelper.isHttpMethod(methodDoc)) {
				putInMappingAndMethods(classDocPath, methodDoc);
			}
		}
	}
	
	/**
	 * This method puts the mapping value and the methodDoc in the map
	 */
	private void putInMappingAndMethods(String mappingValue, MethodDoc methodDoc) {
		if (mappingAndMethods.containsKey(mappingValue)) {
			mappingAndMethods.get(mappingValue).add(methodDoc);
		} else {
			List<MethodDoc> list = new ArrayList<>();
			list.add(methodDoc);
			mappingAndMethods.put(mappingValue, list);
		}
	}
	
	/**
	 * This method creates a path for a single resource, sets its path parameters, creates the operations
	 * and put it in the map
	 */
	private void createPath() {
		for (Entry<String, List<MethodDoc>> entry : mappingAndMethods.entrySet()) {
			List<MethodDoc> methodDocs = entry.getValue();
			Path path = new Path();
			
			path.setParameters(getPathParameters(methodDocs.get(0).containingClass()));
			createOperation(methodDocs, path);
			
			paths.put(entry.getKey(), path);
		}
	}
	
	/**
	 * This method gets all parameters from a resource and its parent resources.
	 */
	private List<Parameter> getPathParameters(ClassDoc classDoc) {
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
		}
		return new ArrayList<>(parameters);
	}
	
	/**
	 * This method gets the path parameter from the fields of a ClassDoc
	 */
	private List<Parameter> getPathParameterFromField(ClassDoc classDoc) {
		return Arrays.asList(classDoc.fields(false)).stream()
			.filter(f -> CommonHelper.hasAnnotation(f, pathParameterHandler.getHttpParameterType()))
			.map(f -> pathParameterHandler.createPathParameterFromField(f))
			.collect(Collectors.toList());
	}
	
	/**
	 * This method gets the path parameter from the parameter of a method
	 */
	private List<Parameter> getPathParameterFromMethodParameter(MethodDoc methodDoc) {
		return Arrays.asList(methodDoc.parameters()).stream()
			.filter(p -> CommonHelper.hasAnnotation(p, pathParameterHandler.getHttpParameterType()))
			.map(p -> pathParameterHandler.createNewSwaggerParameter(p, methodDoc))
			.collect(Collectors.toList());
	}
	
	/**
	 * This method creates the operations for one resource
	 */
	private void createOperation(List<MethodDoc> methodDocs, Path path) {
		for (MethodDoc methodDoc : methodDocs) {
			Operation operation = operationParser.createOperation(methodDoc);
			setOperationToPath(path, operation, methodDoc);
		}
	}
	
	/**
	 * This method sets an operation to the path model
	 */
	private void setOperationToPath(Path path, Operation operation, MethodDoc methodDoc) {
		Arrays.asList(HttpMethodHandler.values()).stream()
			.filter(hmh -> hmh.getFullName().equals(ParserHelper.getFullHttpMethod(methodDoc)))
			.forEach(hmh -> hmh.setOperationToPath(path, operation));
	}
}
