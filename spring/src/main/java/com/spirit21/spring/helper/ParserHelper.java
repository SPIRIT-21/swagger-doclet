package com.spirit21.spring.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spirit21.common.CommonConsts;
import com.spirit21.common.helper.AnnotationHelper;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.spring.handler.annotation.HttpMethodHandler;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;

/**
 * A utility class which contains methods helping during the parsing process.
 * 
 * @author mweidmann
 */
public class ParserHelper {

	/**
	 * Private constructor to hide the implicit public one.
	 */
	private ParserHelper() { }
	
	/**
	 * Checks if a ProgramElementDoc is a Spring Boot controller. Therefore the annotations
	 * are checked for RestController or Controller.
	 * 
	 * @param programElementDoc The element which should be checked.
	 * @return True if it is a Controller, otherwise false.
	 */
	public static boolean isController(ProgramElementDoc programElementDoc) {
		return CommonHelper.hasAnnotation(programElementDoc, RestController.class.getName())
				|| CommonHelper.hasAnnotation(programElementDoc, Controller.class.getName());
	}
	
	/**
	 * Checks if a ProgramElementDoc is annotated by a RequestMapping or HttpMapping annotation.
	 * 
	 * @param programElementDoc The element which should be checked.
	 * @return True if it is annotated, otherwise false.
	 */
	public static boolean isHttpMethod(ProgramElementDoc programElementDoc) {
		return CommonHelper.hasAnnotation(programElementDoc, RequestMapping.class.getName())
				|| getFullHttpMappingAnnotation(programElementDoc) != null;
	}
	
	/**
	 * Gets all mappings (that means including the controller mapping path) of a method.
	 * For example if the controller has the mapping "/controller" and the method these mappings
	 * { "/mapping1", "/mapping2" }, then these paths are generated: { "/controller/mapping1", "/controller/mapping2"}.
	 * 
	 * @param controllerMapping The mapping of the controller.
	 * @param methodDoc The method from which all the mappings are generated.
	 * 
	 * @return A list which contains all full-mappings of a MethodDoc.
	 */
	// TODO: methodMappingAnnotationValues is a redundant computation.
	public static List<String> getAllFullMappingsOfMethod(String controllerMapping, MethodDoc methodDoc) {
		AnnotationValue[] methodMappingAnnotationValues = getMappingValue(methodDoc);
		
		return Arrays.asList(methodMappingAnnotationValues).stream()
				.map(methodMappingAnnotationValue -> (String) CommonHelper.getAnnotationValueObject(methodMappingAnnotationValue))
				.filter(Objects::nonNull)
				.map(methodMapping -> controllerMapping + "/" + methodMapping)
				.map(CommonHelper::replaceMultipleSlashes)
				.collect(Collectors.toList());
	}

	/**
	 * This method gets the full HTTP-Method of a programElementDoc (classDoc,
	 * methodDoc) (e.g. org.springframework.web.bind.annotation.GetMapping...)
	 */
	/**
	 * 
	 * 
	 * @param programElementDoc
	 * @return
	 */
	public static String getFullHttpMappingAnnotation(ProgramElementDoc programElementDoc) {
		return Arrays.asList(HttpMethodHandler.values()).stream()
				.map(HttpMethodHandler::getFullHttpMethodName)
				.filter(fullHttpMethodName -> CommonHelper.hasAnnotation(programElementDoc, fullHttpMethodName))
				.findFirst()
				.orElse(null);
	}

	/**
	 * This method gets the value/path of the mapping annotation of the
	 * programElementDoc
	 */
	public static AnnotationValue[] getMappingValue(ProgramElementDoc programElementDoc) {
		String annotation = null;

		if (CommonHelper.hasAnnotation(programElementDoc, RequestMapping.class.getName())) {
			annotation = RequestMapping.class.getName();
		} else if (getFullHttpMappingAnnotation(programElementDoc) != null) {
			annotation = getFullHttpMappingAnnotation(programElementDoc);
		}
		
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());

		AnnotationValue[] value = null;
		

		value = (AnnotationValue[]) getAnnotationValueOfTwoSpecifics(annotationHelper, annotation, 
				CommonConsts.ANNOTATION_PROPERTY_NAME_VALUE, com.spirit21.spring.Consts.PATH);

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
			return CommonHelper.getAnnotationValueObject(aValue);
		} else {
			aValue = annotationHelper.getAnnotationValue(annotation, specificTwo);

			if (aValue != null) {
				return CommonHelper.getAnnotationValueObject(aValue);
			}
		}
		return null;
	}

	/**
	 * This method gets the simple http Method, e.g.: get, post
	 */
	public static String[] getHttpMethods(ProgramElementDoc programElementDoc) {
		String httpMappingAnnotation = getFullHttpMappingAnnotation(programElementDoc);

		if (CommonHelper.hasAnnotation(programElementDoc, RequestMapping.class.getName())) {
			AnnotationValue aValue = CommonHelper.getAnnotationValue(programElementDoc, 
					RequestMapping.class.getName(), com.spirit21.spring.Consts.METHOD);
			AnnotationValue[] aValues = (AnnotationValue[]) CommonHelper.getAnnotationValueObject(aValue);
			
			return Arrays.asList(aValues).stream()
					.map(annotationValue -> (FieldDoc) CommonHelper.getAnnotationValueObject(annotationValue))
					.map(FieldDoc::name)
					.map(String::toLowerCase)
					.toArray(String[]::new);
		
		} else if (httpMappingAnnotation != null) {
			return Arrays.asList(HttpMethodHandler.values()).stream()
					.filter(hmh -> hmh.getFullHttpMethodName().equals(httpMappingAnnotation))
					.map(HttpMethodHandler::getSimpleHttpMethodName)
					.toArray(String[]::new);
		}
		return null;
	}
}