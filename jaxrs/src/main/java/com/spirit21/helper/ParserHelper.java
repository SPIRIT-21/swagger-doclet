package com.spirit21.helper;

import java.util.Arrays;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;

import com.spirit21.Consts;
import com.spirit21.handler.annotation.HttpMethodHandler;
import com.spirit21.parser.Parser;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;

public class ParserHelper {

	/**
	 * This method checks if a programElementDoc (classDoc, methodDoc) has a specific annotation
	 */
	public static boolean hasAnnotation(ProgramElementDoc programElementDoc, String annotation) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		return annotationHelper.isAnnotatedBy(annotation);
	}

	/**
	 * This method checks if a parameter has a specific annotation
	 */
	public static boolean hasAnnotation(Parameter parameter, String annotation) {
		AnnotationHelper annotationHelper = new AnnotationHelper(parameter.annotations());
		return annotationHelper.isAnnotatedBy(annotation);
	}
	
	/**
	 * This method gets the annotationValue of a specific annotation of a programElementDoc
	 */
	public static String getAnnotationValue(ProgramElementDoc programElementDoc, String annotationType, String specificAnnotation) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		return annotationHelper.getAnnotationValue(annotationType, specificAnnotation);
	}
	
	/**
	 * This method gets the annotationValue of a specific annotation of a parameter
	 */
	public static String getAnnotationValue(Parameter parameter, String annotationType, String specificAnnotation) {
		AnnotationHelper annotationHelper = new AnnotationHelper(parameter.annotations());
		return annotationHelper.getAnnotationValue(annotationType, specificAnnotation);
	}
	
	/**
	 * This method checks if a resource has any methods with HTTP-annotations
	 */
	public static boolean isResource(ClassDoc classDoc) {
		for (MethodDoc methodDoc : classDoc.methods()) {
			if (hasHttpMethod(methodDoc)) {
				return true;
			}
		}
		return false;
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
				.filter(m -> !hasHttpMethod(m) && m.returnType().equals(classDoc))
				.map(m -> getAnnotationValue(m, Path.class.getName(), Consts.VALUE))
				.forEach(v -> {
					sb.append("/" + v);
					sb.insert(0, getPath(parent));
				});
			
			return replaceQuotationMarkAndSlashes(sb.toString());
		} else {
			String annotationValue = "/" + getAnnotationValue(classDoc, Path.class.getName(), Consts.VALUE);
			return replaceQuotationMarkAndSlashes(annotationValue);			
		}
	}
	
	/**
	 * This method helps to replace quotation marks and slashes
	 */
	private static String replaceQuotationMarkAndSlashes(String replace) {
		return replace.replaceAll(Consts.QUOTATION_MARK, Consts.EMPTY_STRING).replaceAll(Consts.SLASHES, Consts.SLASHES_REPLACE);
	}
	
	/**
	 * This method helps to replace quotation marks
	 */ 
	public static String replaceQuotationMarks(String replace) {
		return replace.replaceAll(Consts.QUOTATION_MARK, Consts.EMPTY_STRING);
	}
	
	/**
	 * This method adds a classDoc to the definitionClassDoc list in the Parser
	 */
	public static void addToDefinitionList(ClassDoc classDoc) {
		if (classDoc != null && !Parser.definitionClassDocs.contains(classDoc)) {
			Parser.definitionClassDocs.add(classDoc);
		}
	}
	
	// FROM HERE NO COMMON METHODS
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
	 * This method checks if a programElementDoc has a HTTP-method-annotation (javax.ws.rs.GET/POST/PUT/DELETE)
	 */
	public static boolean hasHttpMethod(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		for (HttpMethodHandler hmh : HttpMethodHandler.values()) {
			if (annotationHelper.isAnnotatedBy(hmh.getFullName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method gets the full HTTP-method of a programElementDoc (javax.ws.rs.GET/POST..)
	 */
	public static String getFullHttpMethod(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		for (HttpMethodHandler httpMethod : HttpMethodHandler.values()) {
			if (annotationHelper.isAnnotatedBy(httpMethod.getFullName())) {
				return httpMethod.getFullName();
			}
		}
		return null;
	}

	/**
	 * This method gets the simple HTTP-method of a programElementDoc (GET/POST/PUT/DELETE)
	 */
	public static String getSimpleHttpMethod(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		for (HttpMethodHandler httpMethod : HttpMethodHandler.values()) {
			if (annotationHelper.isAnnotatedBy(httpMethod.getFullName())) {
				return httpMethod.getSimpleName();
			}
		}
		return null;
	}
}