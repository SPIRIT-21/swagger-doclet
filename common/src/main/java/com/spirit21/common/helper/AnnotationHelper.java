package com.spirit21.common.helper;

import java.util.Arrays;
import java.util.List;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.AnnotationValue;

/**
 * A helper which provides functionality to handle the annotations of a ProgramElementDoc.
 * 
 * @author mweidmann
 */
public class AnnotationHelper {
	
	private final List<AnnotationDesc> annotations;

	public AnnotationHelper(AnnotationDesc[] annotations) {
		this.annotations = Arrays.asList(annotations);
	}
	
	/**
	 * Checks if a ProgramElementDoc is annotated by a specific annotation. The name of the annotation is passed to the method.
	 * 
	 * @param annotationName The name of the annotation for what the annotations of the ProgramElementDoc will be searched.
	 * @return True if the ProgramElementDoc is annotated with the annotation otherwise false.
	 */
	public boolean isAnnotatedBy(String annotationName) {
		return getAnnotation(annotationName) != null;
	}
	
	/**
	 * Finds an AnnotationDesc by the passed name.
	 * Then it gets the value of a property of the annotation by the passed name.  
	 * 
	 * @param annotationName The name of the annotation.
	 * @param annotationValueName The name of the property of the annotation.
	 * @return A found value of the property or null if it does not exist.
	 */
	public AnnotationValue getAnnotationValue(String annotationName, String annotationValueName) {
		AnnotationDesc annotation = getAnnotation(annotationName);
		
		if (annotation == null) {
			return null;
		}
		
		return Arrays.asList(annotation.elementValues()).stream()
				.filter(elementValuePair -> elementValuePair.element().name().equals(annotationValueName))	
				.map(ElementValuePair::value)
				.findFirst()
				.orElse(null);
	}
	
	/**
	 * Gets an AnnotationDesc by a string from a ProgramElementDoc. 
	 * 
	 * @param annotationName The name of the annotation which will be searched.
	 * @return A found AnnotationDesc or null if nothing was found.
	 */
	public AnnotationDesc getAnnotation(String annotationName) {
		return annotations.stream()
				.filter(annotation -> annotation.annotationType().qualifiedTypeName().equals(annotationName))
				.findFirst()
				.orElse(null);
	}
}
