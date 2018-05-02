package com.spirit21.common.helper;

import java.util.List;

import com.spirit21.common.Consts;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
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
	
	/**
	 * This method helps to replace multiple slashes with one
	 */
	public static String replaceSlashes(String replace) {
		return replace.replaceAll(Consts.SLASHES, Consts.SLASHES_REPLACE);
	}
}
