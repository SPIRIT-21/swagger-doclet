package com.spirit21.common.helper;

import com.spirit21.common.CommonConsts;
import com.spirit21.common.parser.AbstractParser;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;

/**
 * A helper class with only static helper methods. These helper functions are in the common 
 * package because they are partially needed across the project.
 * 
 * @author mweidmann
 */
public class CommonHelper {
	
	/**
	 * Checks if a ProgramElementDoc (ClassDoc, MethodDoc) is annotated by a specific annotation.
	 * 
	 * @param programElementDoc The element which should be checked if it is annotated.
	 * @param annotationName The name of the annotation.
	 * @return True if it is annotated, otherwise false.
	 */
	public static boolean hasAnnotation(ProgramElementDoc programElementDoc, String annotationName) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		return annotationHelper.isAnnotatedBy(annotationName);
	}
	
	/**
	 * Gets the value for an annotation of a ProgramElementDoc (ClassDoc, MethodDoc).
	 * 
	 * @param programElementDoc The element from which the annotation will be taken.
	 * @param annotationName The name of the annotation.
	 * @param annotationValueName The name of the property of the annotation.
	 * @return The value of the property of the annotation or null if it does not exist or the annotation is not present. 
	 */
	public static AnnotationValue getAnnotationValue(ProgramElementDoc programElementDoc, String annotationName, String annotationValueName) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		return annotationHelper.getAnnotationValue(annotationName, annotationValueName);
	}

	/**
	 * Checks if a Parameter(Doc) is annotated by a specific annotation.
	 * 
	 * @param parameter The parameter which should be checked if it is annotated.
	 * @param annotationName The name of the annotation.
	 * @return True if it is annotated, otherwise false.
	 */
	public static boolean hasAnnotation(Parameter parameter, String annotationName) {
		AnnotationHelper annotationHelper = new AnnotationHelper(parameter.annotations());
		return annotationHelper.isAnnotatedBy(annotationName);
	}
	
	/**
	 * Gets the value for an annotation of a Parameter(Doc).
	 * 
	 * @param parameter The parameter from which the annotation will be taken.
	 * @param annotation The name of the annotation.
	 * @param specificAnnotation The name of the property of the annotation.
	 * @return The value of the property of the annotation or null if it does not exist or the annotation is not present.
	 */
	public static AnnotationValue getAnnotationValue(Parameter parameter, String annotation, String specificAnnotation) {
		AnnotationHelper annotationHelper = new AnnotationHelper(parameter.annotations());
		return annotationHelper.getAnnotationValue(annotation, specificAnnotation);
	}
	
	/**
	 * Gets the value out of an AnnotationValue object.
	 * Mostly it is a String but it can be any other type.
	 * 
	 * @param annotationValue The AnnotationValue object from where the value will be taken.
	 * @return The value of the AnnotationValue object or null if the AnnotationValue is null.
	 */
	public static Object getAnnotationValueObject(AnnotationValue annotationValue) {
		return annotationValue != null ? annotationValue.value() : null;
	}
	
	/**
	 * Adds a ClassDoc to the definitionClassDoc list in the Parser if it does not exist in the list.
	 * 
	 * @param classDoc The ClassDoc which should be added to the list.
	 */
	public static void addToDefinitionList(ClassDoc classDoc) {
		if (classDoc != null && !AbstractParser.DEFINITION_CLASS_DOCS.contains(classDoc)) {
			AbstractParser.DEFINITION_CLASS_DOCS.add(classDoc);
		}
	}
	
	/**
	 * Replaces multiple successive slashes in a string with one slash.
	 * Reason is for example the naming of a resource path. Some people write a trailing slash, others don't.
	 * 
	 * @param replace The String in which the multiple slashes should be replaced.
	 * @return A new string with the replaced slashes.
	 */
	public static String replaceMultipleSlashes(String replace) {
		return replace.replaceAll(CommonConsts.REGEX_SLASHES, CommonConsts.REGEX_SLASHES_REPLACE);
	}
}
