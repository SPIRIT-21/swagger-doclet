package com.spirit21.helper;

import java.util.Arrays;
import java.util.List;

import com.sun.javadoc.AnnotationDesc;

public class AnnotationHelper {

	private final List<AnnotationDesc> annotations;
	
	public AnnotationHelper(AnnotationDesc[] annotations) {
		this.annotations = Arrays.asList(annotations);
	}
	
	// This method checks if a programElementDoc (methodDoc, classDoc) is annotated by an annotationType
	public boolean isAnnotatedBy(String annotationType) {
		return getAnnotation(annotationType) != null;
	}
	
	// This method gets the annotation value of a annotation of a programElementDoc
	public String getAnnotationValue(String annotationType) {
		AnnotationDesc annotation = getAnnotation(annotationType);
		if (annotation != null) {
			return annotation.elementValues()[0].value().toString();
		} else {
			return null;
		}
	}
	
	// This method gets the AnnotationDesc of a programElementDoc 
	private AnnotationDesc getAnnotation(String qualifiedTypeName) {
		return annotations.stream()
			.filter(a -> a.annotationType().qualifiedTypeName().equals(qualifiedTypeName))
			.findFirst()
			.orElse(null);
	}
}