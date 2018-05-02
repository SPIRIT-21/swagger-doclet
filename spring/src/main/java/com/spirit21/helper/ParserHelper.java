package com.spirit21.helper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spirit21.common.Consts;
import com.spirit21.common.helper.AnnotationHelper;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.handler.annotation.HttpMethodHandler;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;

public class ParserHelper extends CommonHelper {

	/**
	 * This method checks if a programElementDoc has a RequestMapping or and HttpMapping annotation
	 */
	public static boolean isHttpMethod(ProgramElementDoc programElementDoc) {
		return hasRequestMappingAnnotation(programElementDoc) 
				|| getHttpMappingAnnotation(programElementDoc) != null;
	}
	
	/**
	 * This method gets the path of a method
	 */
	public static List<String> getPath(String controllerMapping, MethodDoc methodDoc) {
		AnnotationValue[] methodValues = getMappingValue(methodDoc);
		
		return Arrays.asList(methodValues).stream()
				.map(aValue -> (String) getAnnotationValueObject(aValue))
				.map(string -> controllerMapping + "/" + string)
				.map(ParserHelper::replaceSlashes)
				.collect(Collectors.toList());
	}

	/**
	 * This method checks if a programElementDoc (classDoc, methodDoc) has a
	 * SpringBootApplication annotation
	 */
	public static boolean hasSpringBootApplicationAnnotation(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		return annotationHelper.isAnnotatedBy(SpringBootApplication.class.getName());
	}

	/**
	 * This method checks if a programElementDoc (classDoc, methodDoc) is a
	 * Controller
	 */
	public static boolean hasControllerAnnotation(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());

		return annotationHelper.isAnnotatedBy(RestController.class.getName())
				|| annotationHelper.isAnnotatedBy(Controller.class.getName());
	}

	/**
	 * This method checks if a programElementDoc (classDoc, methodDoc) has a
	 * RequestMapping annotation
	 */
	public static boolean hasRequestMappingAnnotation(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		return annotationHelper.isAnnotatedBy(RequestMapping.class.getName());
	}

	/**
	 * This method gets the full HTTP-Method of a programElementDoc (classDoc,
	 * methodDoc) (e.g. org.springframework.web.bind.annotation.GetMapping...)
	 */
	public static String getHttpMappingAnnotation(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());

		for (HttpMethodHandler hmh : HttpMethodHandler.values()) {
			if (annotationHelper.isAnnotatedBy(hmh.getFullName())) {
				return hmh.getFullName();
			}
		}
		return null;
	}

	/**
	 * This method gets the value/path of the mapping annotation of the
	 * programElementDoc
	 */
	public static AnnotationValue[] getMappingValue(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());

		AnnotationValue[] value = null;
		String annotation = null;

		if (hasRequestMappingAnnotation(programElementDoc)) {
			annotation = RequestMapping.class.getName();
		} else if (getHttpMappingAnnotation(programElementDoc) != null) {
			annotation = getHttpMappingAnnotation(programElementDoc);
		}

		value = (AnnotationValue[]) getAnnotationValueOfTwoSpecifics(annotationHelper, annotation, 
				Consts.VALUE, com.spirit21.Consts.PATH);

		if (value != null && value.length != 0) {
			return value;
		}
		return new AnnotationValue[0];
	}

	/**
	 * This method helps to get the value of an annotation if two specific values are the same
	 */
	public static Object getAnnotationValueOfTwoSpecifics(AnnotationHelper annotationHelper, String annotation,
			String specificOne, String specificTwo) {
		AnnotationValue aValue = annotationHelper.getAnnotationValue(annotation, specificOne);

		if (aValue != null) {
			return ParserHelper.getAnnotationValueObject(aValue);
		} else {
			aValue = annotationHelper.getAnnotationValue(annotation, specificTwo);

			if (aValue != null) {
				return ParserHelper.getAnnotationValueObject(aValue);
			}
		}
		return null;
	}

	/**
	 * This method gets the simple http Method, e.g.: get, post
	 */
	public static String[] getHttpMethods(ProgramElementDoc programElementDoc) {
		String httpMappingAnnotation = getHttpMappingAnnotation(programElementDoc);

		if (hasRequestMappingAnnotation(programElementDoc)) {
			AnnotationValue aValue = getAnnotationValue(programElementDoc, 
					RequestMapping.class.getName(), com.spirit21.Consts.METHOD);
			AnnotationValue[] aValues = (AnnotationValue[]) getAnnotationValueObject(aValue);
			
			return Arrays.asList(aValues).stream()
					.map(annotationValue -> (FieldDoc) getAnnotationValueObject(annotationValue))
					.map(FieldDoc::name)
					.map(String::toLowerCase)
					.toArray(String[]::new);
		
		} else if (httpMappingAnnotation != null) {
			return Arrays.asList(HttpMethodHandler.values()).stream()
					.filter(hmh -> hmh.getFullName().equals(httpMappingAnnotation))
					.map(HttpMethodHandler::getSimpleName)
					.toArray(String[]::new);
		}
		return null;
	}
}