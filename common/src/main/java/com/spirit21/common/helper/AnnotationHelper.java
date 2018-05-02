package com.spirit21.common.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;
import com.sun.javadoc.AnnotationValue;

public class AnnotationHelper {
	
	private final List<AnnotationDesc> annotations;

	public AnnotationHelper(AnnotationDesc[] annotations) {
		this.annotations = Arrays.asList(annotations);
	}
	
	/**
	 * This method checks if a programElementDoc (methodDoc, classDoc) is annotated by an annotationType
	 */
	public boolean isAnnotatedBy(String annotationType) {
		return getAnnotation(annotationType) != null;
	}
	
	/**
	 * This method gets the annotation value of a annotation of a programElementDoc
	 */
	public AnnotationValue getAnnotationValue(String annotationType, String specificAnnotation) {
		AnnotationDesc annotation = getAnnotation(annotationType);
		
		if (annotation != null) {
			Optional<AnnotationValue> opt =  Arrays.asList(annotation.elementValues()).stream()
					.filter(evp -> evp.element().name().equals(specificAnnotation))	
					.map(ElementValuePair::value)
					.findFirst();
			if (opt.isPresent()) {
				return opt.get();
			}
		}
		return null;
	}
	
	/**
	 * This method gets the AnnotationDesc of a programElementDoc
	 */
	public AnnotationDesc getAnnotation(String qualifiedTypeName) {
		return annotations.stream()
				.filter(a -> a.annotationType().qualifiedTypeName().equals(qualifiedTypeName))
				.findFirst()
				.orElse(null);
	}
}
