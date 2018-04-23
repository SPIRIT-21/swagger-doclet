package com.spirit21.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;

import com.spirit21.Consts;
import com.spirit21.handler.annotation.HttpMethodHandler;
import com.spirit21.parser.Parser;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;

public class ParserHelper {
	
	// COMMON
	/**
	 * This method checks if a programElementDoc (classDoc, methodDoc) has a specific annotation
	 */
	public static boolean hasAnnotation(ProgramElementDoc programElementDoc, String annotation) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		return annotationHelper.isAnnotatedBy(annotation);
	}
	
	/**
	 * This method gets the annotationValue of a specific annotation of a programElementDoc
	 */
	public static AnnotationValue getAnnotationValue(ProgramElementDoc programElementDoc, String annotation, String specificAnnotation) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		return annotationHelper.getAnnotationValue(annotation, specificAnnotation);
	}

	/**
	 * This method checks if a parameter has a specific annotation
	 */
	public static boolean hasAnnotation(Parameter parameter, String annotation) {
		AnnotationHelper annotationHelper = new AnnotationHelper(parameter.annotations());
		return annotationHelper.isAnnotatedBy(annotation);
	}
	
	/**
	 * This method gets the annotationValue of a specific annotation of a parameter
	 */
	public static AnnotationValue getAnnotationValue(Parameter parameter, String annotation, String specificAnnotation) {
		AnnotationHelper annotationHelper = new AnnotationHelper(parameter.annotations());
		return annotationHelper.getAnnotationValue(annotation, specificAnnotation);
	}
	
	/**
	 * This method gets the value of the AnnotationValue object
	 */
	public static Object getAnnotationValueObject(AnnotationValue aValue) {
		if (aValue != null) {
			return aValue.value();
		}
		return null;
	}
	
	/**
	 * This method adds a classDoc to the definitionClassDoc list in the Parser
	 */
	public static void addToDefinitionList(List<ClassDoc> definitionClassDocs, ClassDoc classDoc) {
		if (classDoc != null && !definitionClassDocs.contains(classDoc)) {
			definitionClassDocs.add(classDoc);
		}
	}
	
	// EVENTUELL COMMON
	/**
	 * This method helps to replace multiple slashes with one
	 */
	private static String replaceSlashes(String replace) {
		return replace.replaceAll(Consts.SLASHES, Consts.SLASHES_REPLACE);
	}
	
	/**
	 * This method checks if a resource has any methods with HTTP-annotations
	 */
	public static boolean isResource(ClassDoc classDoc) {
		for (MethodDoc methodDoc : classDoc.methods()) {
			if (isHttpMethod(methodDoc)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method gets the simple http method of a programElementDoc
	 */
	public static String getSimpleHttpMethod(ProgramElementDoc programElementDoc) {
		HttpMethodHandler httpMethodHandler = getHttpMethodHandler(programElementDoc);
		
		if (httpMethodHandler != null) {
			return httpMethodHandler.getSimpleName();
		}
		return null;
	}
	
	/**
	 * This method gets the full http method of a programElementDoc
	 */
	public static String getFullHttpMethod(ProgramElementDoc programElementDoc) {
		HttpMethodHandler httpMethodHandler = getHttpMethodHandler(programElementDoc);
		
		if (httpMethodHandler != null) {
			return httpMethodHandler.getFullName();
		}
		return null;
	}
	
	/**
	 * This method gets the right handler for the programElementDoc
	 */
	private static HttpMethodHandler getHttpMethodHandler(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		
		for (HttpMethodHandler httpMethodHandler : HttpMethodHandler.values()) {
			if (annotationHelper.isAnnotatedBy(httpMethodHandler.getFullName())) {
				return httpMethodHandler;
			}
		}
		return null;
	}
	
	/**
	 * This method checks if a programElementDoc has a HTTP-method-annotation (javax.ws.rs.GET/POST/PUT/DELETE)
	 */
	public static boolean isHttpMethod(ProgramElementDoc programElementDoc) {
		return getFullHttpMethod(programElementDoc) != null;
	}
	
	// NOT COMMON
	/**
	 * This method checks if a programElementDoc (classDoc, methodDoc) has a javax.ws.rs.ApplicationPath annotation
	 */
	public static boolean hasApplicationPathAnnotation(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		return annotationHelper.isAnnotatedBy(ApplicationPath.class.getName());
	}
	
	/**
	 * This method checks if a programElementDoc has the javax.ws.rs.Path annotation
	 */
	public static boolean hasPathAnnotation(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		return annotationHelper.isAnnotatedBy(Path.class.getName());
	}
	
	/**
	 * This method returns the parentResourceClassDoc of any (sub..)ResourceClassDoc
	 */
	public static ClassDoc getParentClassDoc(ClassDoc classDoc) {
		if (Parser.resourceClassDocs.containsKey(classDoc)) {
			if (Parser.resourceClassDocs.get(classDoc) == null) {
				return classDoc;
			} else {
				return getParentClassDoc(Parser.resourceClassDocs.get(classDoc));
			}
		}
		return null;
	}

	/**
	 * This method returns the complete path of any classDoc
	 */
	public static String getPath(ClassDoc classDoc) {
		ClassDoc parent = Parser.resourceClassDocs.get(classDoc);
		
		// check if classDoc has a parent
		if (parent != null) {
			StringBuilder sb = new StringBuilder();
			
			// iterate through parent methods and get the method which returns the subResource
			Arrays.asList(parent.methods()).stream()
				.filter(ParserHelper::hasPathAnnotation)
				.filter(m -> !isHttpMethod(m) && m.returnType().equals(classDoc))
				.map(m -> getAnnotationValue(m, Path.class.getName(), Consts.VALUE))
				.filter(Objects::nonNull)
				.map(ParserHelper::getAnnotationValueObject)
				.filter(Objects::nonNull)
				.forEach(aValue -> {
					sb.append("/" + aValue);
					sb.insert(0, getPath(parent));
				});
			
			return replaceSlashes(sb.toString());
		} else {
			AnnotationValue aValue = getAnnotationValue(classDoc, Path.class.getName(), Consts.VALUE);
			String value = "/" + (String) getAnnotationValueObject(aValue);
			return replaceSlashes(value);
		}
	}
}