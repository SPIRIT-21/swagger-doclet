package com.spirit21.spring.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RequestMapping;

import com.spirit21.common.helper.CommonHelper;
import com.spirit21.spring.helper.ParserHelper;
import com.sun.javadoc.ClassDoc;

import io.swagger.models.Swagger;
import io.swagger.models.Tag;

/**
 * Parses the controller ClassDocs to create Swagger tags.
 * The needed information is located in the Controller annotations of the ClassDoc.
 * 
 * @author mweidmann
 */
public class TagParser {

	/**
	 * Starts the conversion process for swagger tags. Firstly, the ClassDocs will be sorted in 
	 * ClassDocs with a RequestMapping annotation and in ClassDocs without. After that these two lists
	 * are treated individually.
	 * 
	 * @param swagger The Swagger object in which the generated tags will be set.
	 */
	protected void run(Swagger swagger) {
		Set<Tag> swaggerTags = new HashSet<>();

		List<ClassDoc> controllerWithRequestMapping = getControllerWithRequestMapping();
		List<ClassDoc> controllerWithoutRequestMapping = new ArrayList<>(Parser.CONTROLLER_CLASS_DOCS);
		controllerWithoutRequestMapping.removeAll(controllerWithRequestMapping);
		
		handleControllerWithRequestMapping(controllerWithRequestMapping, swaggerTags);
		handleControllerWithoutRequestMapping(controllerWithoutRequestMapping, swaggerTags);
		
		swagger.setTags(new ArrayList<>(swaggerTags));
	}
	
	/**
	 * Returns all ClassDocs/Controller which are annotated with a RequestMapping annotation.
	 * 
	 * @return A list containing all controller ClassDocs which are annotated with a RequestMapping annotation.
	 */
	private List<ClassDoc> getControllerWithRequestMapping() {
		return Parser.CONTROLLER_CLASS_DOCS.stream()
				.filter(classDoc -> CommonHelper.hasAnnotation(classDoc, RequestMapping.class.getName()))
				.collect(Collectors.toList());
	}

	/**
	 * Handles ClassDocs annotated with a RequestMapping annotation by creating swagger 
	 * tags from the value of the RequestMapping annotation.
	 * 
	 * @param controllerWithRequestMapping A list of ClassDocs annotated with a RequestMapping annotation.
	 * @param swaggerTags The set in which the generated swagger tags will be added.
	 */
	private void handleControllerWithRequestMapping(List<ClassDoc> controllerWithRequestMapping, Set<Tag> swaggerTags) {
		controllerWithRequestMapping.stream()
			.map(ParserHelper::getMappingPath)
			.flatMap(Arrays::stream)
			.map(mappingPath -> (String) CommonHelper.getAnnotationValueObject(mappingPath))
			.map(this::createSwaggerTag)
			.filter(Objects::nonNull)
			.forEach(swaggerTags::add);
	}
	
	/**
	 * Handle classDocs without the RequestMapping annotation
	 * It iterates over the methods and creates with their mapping value the tag
	 */
	/**
	 * Handles ClassDocs which are not annotated with a RequestMapping annotation by iterating
	 * over the MethodDocs of the ClassDoc and creating a swagger tag from the value of the 
	 * RequestMapping annotation of the method.
	 * 
	 * @param controllerWithoutRequestMapping A list of ClassDocs which are not annotated with a RequestMapping
	 * @param swaggerTags The set in which the generated swagger tags will be added.
	 */
	private void handleControllerWithoutRequestMapping(List<ClassDoc> controllerWithoutRequestMapping, Set<Tag> swaggerTags) {
		controllerWithoutRequestMapping.stream()
			.map(ClassDoc::methods)
			.flatMap(Arrays::stream)
			.map(ParserHelper::getMappingPath)
			.flatMap(Arrays::stream)
			.map(mappingPath -> (String) CommonHelper.getAnnotationValueObject(mappingPath))
			.map(this::createSwaggerTag)
			.filter(Objects::nonNull)
			.forEach(swaggerTags::add);
	}

	/**
	 * Creates an swagger tag out of an annotation value. The annotation value is used to
	 * get the name of the swagger tag. The annotation value will be matched with an 
	 * regular expression to get only the "root" path. 
	 * For example if the mapping value is something like "/test/foo/" the regular expression
	 * matches "test".
	 * 
	 * @param annotationValue The annotation value of which the name for the swagger tag is taken.
	 * @return The newly created swagger tag or null if something failed.
	 */
	private Tag createSwaggerTag(String annotationValue) {
		Matcher matcher = Parser.TAG_NAME_PATTERN.matcher(annotationValue);
		
		if (!matcher.matches()) {
			return null;
		}
		
		String rootPath = matcher.group(1);
		
		Tag swaggerTag = new Tag();
		swaggerTag.setName(rootPath);
		
		return swaggerTag;
	}
}