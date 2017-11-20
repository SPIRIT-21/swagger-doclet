package com.spirit21.helper;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;;

public class AnnotationHelper {
	
	private final AnnotationDesc[] annotations;

	public AnnotationHelper(AnnotationDesc[] annotations) {
		this.annotations = annotations.clone();
	}
	
	public boolean isAnnotatedBy(String annotationType) {
		return getAnnotation(annotationType) != null;
	}
	
	public AnnotationDesc getAnnotation(String qualifiedTypeName) {
		AnnotationDesc found = null;
		for (AnnotationDesc annotation : this.annotations) {
			if (annotation.annotationType().qualifiedTypeName().equals(qualifiedTypeName)) {
				found = annotation;
				break;
			}
		}
		return found;
	}
	
	public String getAnnotationValue(String annotationType, String specificAnnotation) {
		AnnotationDesc annotation = getAnnotation(annotationType);
		if (annotation != null) {
			for (ElementValuePair evp : annotation.elementValues()) {
				if (evp.element().name().equals(specificAnnotation)) {
					return evp.value().toString();
				}
			}
			return null;
		} else {
			return null;
		}
	}
}
