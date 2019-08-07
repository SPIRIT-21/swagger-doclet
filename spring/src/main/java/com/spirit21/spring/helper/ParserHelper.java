package com.spirit21.spring.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spirit21.common.CommonConsts;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.spring.Consts;
import com.spirit21.spring.handler.annotation.HttpMethodHandler;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
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
	 * Checks if a ClassDoc is a Spring Boot controller. Therefore the annotations
	 * are checked for RestController or Controller.
	 * 
	 * @param classDoc The element which should be checked.
	 * @return True if it is a Controller, otherwise false.
	 */
	public static boolean isController(ClassDoc classDoc) {
		return CommonHelper.hasAnnotation(classDoc, RestController.class.getName())
				|| CommonHelper.hasAnnotation(classDoc, Controller.class.getName());
	}
	
	/**
	 * Checks if a MethodDoc is annotated by a RequestMapping or HttpMapping annotation.
	 * 
	 * @param methodDoc The element which should be checked.
	 * @return True if it is annotated, otherwise false.
	 */
	public static boolean hasMappingAnnotation(MethodDoc methodDoc) {
		return getFullMappingAnnotation(methodDoc) != null;
	}
	
	/**
	 * Gets all mappings (that means including the controller mapping path) of a method.
	 * For example if the controller has the mapping "/controller" and the method these mappings
	 * { "/mapping1", "/mapping2" }, then these paths are generated: { "/controller/mapping1", "/controller/mapping2"}.
	 * 
	 * @param controllerMapping The mapping of the controller.
	 * @param methodMappings An array which contains all method mappings.
	 * @return A list which contains all full-mappings of a MethodDoc.
	 */
	public static List<String> getAllFullMappingsOfMethod(String controllerMapping, AnnotationValue[] methodMappings) {
		return Arrays.asList(methodMappings).stream()
				.map(annotationValue -> (String) CommonHelper.getAnnotationValueObject(annotationValue))
				.filter(Objects::nonNull)
				.map(methodMapping -> controllerMapping + "/" + methodMapping)
				.map(CommonHelper::replaceMultipleSlashes)
				.collect(Collectors.toList());
	}
	
	/**
	 * Gets the full qualified mapping name of a ProgramElementDoc if it exists. For example:
	 * - org.springframework.web.bind.annotation.RequestMapping
	 * - org.springframework.web.bind.annotation.GetMapping
	 * - ...
	 * 
	 * @param programElementDoc The element which should checked for a Mapping annotation.
	 * @return The full qualified mapping name of the ProgramElementDoc or null if it does not exist.
	 */
	public static String getFullMappingAnnotation(ProgramElementDoc programElementDoc) {
		if (CommonHelper.hasAnnotation(programElementDoc, RequestMapping.class.getName())) {
			return RequestMapping.class.getName();
		}
		
		return Arrays.asList(HttpMethodHandler.values()).stream()
				.map(HttpMethodHandler::getFullHttpMethodName)
				.filter(fullHttpMethodName -> CommonHelper.hasAnnotation(programElementDoc, fullHttpMethodName))
				.findFirst()
				.orElse(null);
	}
	
	/**
	 * Gets the mapping values/paths of the passed ProgramElementDoc by parsing the properties 
	 * of the mapping annotation.
	 * 
	 * @param programElementDoc The element that has a mapping annotation and of which the properties will be parsed.
	 * @return The found mapping values/paths of the ProgramElementDoc or an empty array if nothing was found.
	 */
	public static AnnotationValue[] getMappingPath(ProgramElementDoc programElementDoc) {
		String annotationName = getFullMappingAnnotation(programElementDoc);
		AnnotationValue[] annotationValue = (AnnotationValue[]) getAnnotationValueOfTwoProperties(programElementDoc, annotationName, 
				CommonConsts.ANNOTATION_PROPERTY_NAME_VALUE, Consts.ANNOTATION_PROPERTY_PATH);

		if (annotationValue != null && annotationValue.length != 0) {
			return annotationValue;
		}
		return new AnnotationValue[0];
	}

	/**
	 * When an annotation has two properties, each of which aliases each other, then this method helps to 
	 * get the correct value.
	 * It first checks whether the first property has a value. If so, it is directly returned.
	 * If not it checks whether the second property has a value. If so, it is directly returned.
	 * If the second property does not exit either, null is returned.
	 * 
	 * @param programElementDoc The element that has the annotation from which the properties are searched.
	 * @param annotationName The name of the annotation.
	 * @param firstProperty The name of the first (alias) property.
	 * @param secondProperty The name of the second (alias) property.
	 * @return The AnnotationValue object of the first property or the second property. Or null if these does not exist.
	 */
	// TODO: redundant != null check because the CommonHelper.getAnnotationValueObject checks for null values.
	public static Object getAnnotationValueOfTwoProperties(ProgramElementDoc programElementDoc, String annotationName, String firstProperty, String secondProperty) {
		AnnotationValue annotationValue = CommonHelper.getAnnotationValue(programElementDoc, annotationName, firstProperty);
		
		if (annotationValue != null) {
			return CommonHelper.getAnnotationValueObject(annotationValue);
		}
		
		annotationValue = CommonHelper.getAnnotationValue(programElementDoc, annotationName, secondProperty);
		return CommonHelper.getAnnotationValueObject(annotationValue);
	}
	
	/**
	 * When an annotation has two properties, each of which aliases each other, then this method helps to 
	 * get the correct value.
	 * It first checks whether the first property has a value. If so, it is directly returned.
	 * If not it checks whether the second property has a value. If so, it is directly returned.
	 * If the second property does not exit either, null is returned.
	 * 
	 * @param parameter The element that has the annotation from which the properties are searched.
	 * @param annotationName The name of the annotation.
	 * @param firstProperty The name of the first (alias) property.
	 * @param secondProperty The name of the second (alias) property.
	 * @return The AnnotationValue object of the first property or the second property. Or null if these does not exist.
	 */
	// TODO: redundant != null check because the CommonHelper.getAnnotationValueObject checks for null values.
	public static Object getAnnotationValueOfTwoProperties(Parameter parameter, String annotationName, String firstProperty, String secondProperty) {
		AnnotationValue annotationValue = CommonHelper.getAnnotationValue(parameter, annotationName, firstProperty);
		
		if (annotationValue != null) {
			return CommonHelper.getAnnotationValueObject(annotationValue);
		}
		
		annotationValue = CommonHelper.getAnnotationValue(parameter, annotationName, secondProperty);
		return CommonHelper.getAnnotationValueObject(annotationValue);
	}
	
	/**
	 * Gets all mappings of a controller by checking the RequestMapping annotation of the
	 * passed ClassDoc.
	 * 
	 * @param classDoc The ClassDoc out of which the mappings should be taken from.
	 * @return A list containing all Mappings of the passed ClassDoc.
	 */
	public static List<String> getAllControllerMappings(ClassDoc classDoc) {
		AnnotationValue[] annotationValues = ParserHelper.getMappingPath(classDoc);
		
		return Arrays.asList(annotationValues).stream()
				.map(annotationValue -> (String) CommonHelper.getAnnotationValueObject(annotationValue))
				.collect(Collectors.toList());
	}
	
	/**
	 * Returns all path parameter names contained in a mapping path.
	 * 
	 * @param pathParameterNamePattern The pattern which parses the path parameter names.
	 * @param path The mapping path which may contain path parameters.
	 * @return A set which contains all path parameter names.
	 */
	public static Set<String> getPathParameterNames(Pattern pathParameterNamePattern, String path) {
		Set<String> pathParameterNames = new HashSet<>();
		Matcher matcher = pathParameterNamePattern.matcher(path);
		
		while (matcher.find()) {
			pathParameterNames.add(matcher.group());
		}
		return pathParameterNames;
	}

	/**
	 * Gets a list of simple HTTP methods of a ProgramElementDoc. First it is checked what
	 * for a mapping annotation is used. Then based on the annotation a list is created, containing
	 * all simple HTTP methods the passed ProgramElementDoc is supporting.
	 * 
	 * @param programElementDoc The ProgramElementDoc which is checked for the HTTP method it supports.
	 * @return A list containing all simple HTTP methods the passed ProgramElementDoc is supporting.
	 */
	public static List<String> getSimpleHttpMethods(ProgramElementDoc programElementDoc) {
		String mappingAnnotation = getFullMappingAnnotation(programElementDoc);
		
		if (!RequestMapping.class.getName().equals(mappingAnnotation)) {
			return Arrays.asList(HttpMethodHandler.values()).stream()
					.filter(httpMethodHandler -> httpMethodHandler.getFullHttpMethodName().equals(mappingAnnotation))
					.map(HttpMethodHandler::getSimpleHttpMethodName)
					.collect(Collectors.toList());
		}
		
		AnnotationValue annotationValue = CommonHelper.getAnnotationValue(programElementDoc, RequestMapping.class.getName(), Consts.ANNOTATION_PROPERTY_METHOD);
		AnnotationValue[] annotationRequestMethods = (AnnotationValue[]) CommonHelper.getAnnotationValueObject(annotationValue);
		
		return Arrays.asList(annotationRequestMethods).stream()
				.map(annotationRequestMethod -> (FieldDoc) CommonHelper.getAnnotationValueObject(annotationRequestMethod))
				.map(FieldDoc::name)
				.map(String::toLowerCase)
				.collect(Collectors.toList());
	}
}
