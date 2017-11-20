package com.spirit21.helper;

import org.springframework.web.bind.annotation.RequestMapping;

import com.spirit21.Consts;
import com.spirit21.handler.annotation.HttpMethodHandler;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;

public class ParserHelper {

	public static boolean hasAnnotation(ProgramElementDoc programElementDoc, String annotation) {
		AnnotationHelper p = new AnnotationHelper(programElementDoc.annotations());
		return p.isAnnotatedBy(annotation);
	}

	public static boolean hasAnnotationValue(ProgramElementDoc programElementDoc, String annotationType,
			String specificAnnotation) {
		return getAnnotationValue(programElementDoc, annotationType, specificAnnotation) != null;
	}

	public static String getAnnotationValue(ProgramElementDoc programElementDoc, String annotationType,
			String specificAnnotation) {
		if (hasAnnotation(programElementDoc, annotationType)) {
			AnnotationHelper p = new AnnotationHelper(programElementDoc.annotations());
			return p.getAnnotationValue(annotationType, specificAnnotation);
		} else {
			return null;
		}
	}

	public static boolean hasHttpMethod(ProgramElementDoc programElementDoc) {
		AnnotationHelper p = new AnnotationHelper(programElementDoc.annotations());
		for (HttpMethodHandler hmh : HttpMethodHandler.values()) {
			if (p.isAnnotatedBy(hmh.getFullName())) {
				return true;
			}
		}
		return hasAnnotationValue(programElementDoc, RequestMapping.class.getName(), Consts.METHOD);
	}

	public static AnnotationDesc getHttpMethod(ProgramElementDoc programElementDoc) {
		for (AnnotationDesc annotation : programElementDoc.annotations()) {
			if (annotation.annotationType().qualifiedTypeName().equals(RequestMapping.class.getName())) {
				return annotation;
			}
			for (HttpMethodHandler hmh : HttpMethodHandler.values()) {
				if (annotation.annotationType().qualifiedTypeName().equals(hmh.getFullName())) {
					return annotation;
				}
			}
		}
		return null;
	}

	public static boolean checkController(ClassDoc classDoc) {
		for (MethodDoc methodDoc : classDoc.methods()) {
			if (hasHttpMethod(methodDoc)) {
				return true;
			}
		}
		return false;
	}

	public static String getPathOrValueOfAnnotation(ProgramElementDoc programElementDoc, String annotationType) {
		StringBuilder sb = new StringBuilder();
		String s1 = getAnnotationValue(programElementDoc, annotationType, Consts.VALUE);
		if (s1 != null) {
			sb.append(s1);
			return sb.toString().replaceAll(Consts.QUOTATION_MARK, Consts.QUOTATION_MARK_REPLACE)
					.replaceAll(Consts.SLASHES, Consts.SLASHES_REPLACE);
		}
		String s2 = getAnnotationValue(programElementDoc, annotationType, Consts.PATH);
		if (s2 != null) {
			sb.append(s2);
			return sb.toString().replaceAll(Consts.QUOTATION_MARK, Consts.QUOTATION_MARK_REPLACE)
					.replaceAll(Consts.SLASHES, Consts.SLASHES_REPLACE);
		}
		return null;
	}

	public static String getFullPath(MethodDoc methodDoc) {
		StringBuilder sb = new StringBuilder();
		String s1 = getPathOrValueOfAnnotation(methodDoc.containingClass(), RequestMapping.class.getName());
		AnnotationDesc annotationDesc = getHttpMethod(methodDoc);
		if (s1 != null && annotationDesc != null) {
			String s2 = getPathOrValueOfAnnotation(methodDoc, annotationDesc.annotationType().qualifiedTypeName());
			sb.append(s1 + "/" + s2);
		}
		return sb.toString().replaceAll(Consts.QUOTATION_MARK, Consts.QUOTATION_MARK_REPLACE).replaceAll(Consts.SLASHES,
				Consts.SLASHES_REPLACE);
	}
}