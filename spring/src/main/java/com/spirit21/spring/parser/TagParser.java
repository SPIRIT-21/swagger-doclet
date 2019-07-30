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

public class TagParser {

	/**
	 * This method sets all tags into the swagger object
	 */
	protected void setTags(Swagger swagger) {
		Set<Tag> tags = new HashSet<>();

		List<ClassDoc> withRequestMapping = getControllerWithRequestMapping();
		List<ClassDoc> withoutRequestMapping = new ArrayList<>(Parser.controllerClassDocs);
		withoutRequestMapping.removeAll(withRequestMapping);
		
		handleWithRequestMapping(withRequestMapping, tags);
		handleWithoutRequestMapping(withoutRequestMapping, tags);
		
		swagger.setTags(new ArrayList<>(tags));
	}
	
	/**
	 * This method gets all controller classDocs with a RequestMapping annotation
	 */
	private List<ClassDoc> getControllerWithRequestMapping() {
		return Parser.controllerClassDocs.stream()
				.filter(classDoc -> CommonHelper.hasAnnotation(classDoc, RequestMapping.class.getName()))
				.collect(Collectors.toList());
	}

	/**
	 * Handle classDocs with the RequestMapping annotation
	 * It gets the mapping value and creates a tag with it
	 */
	private void handleWithRequestMapping(List<ClassDoc> withRequestMapping, Set<Tag> tags) {
		withRequestMapping.stream()
			.map(ParserHelper::getMappingValue)
			.flatMap(Arrays::stream)
			.map(aValue -> (String) aValue.value())
			.map(this::createTag)
			.filter(Objects::nonNull)
			.forEach(tags::add);
	}
	
	/**
	 * Handle classDocs without the RequestMapping annotation
	 * It iterates over the methods and creates with their mapping value the tag
	 */
	private void handleWithoutRequestMapping(List<ClassDoc> withoutRequestMapping, Set<Tag> tags) {
		withoutRequestMapping.stream()
			.map(ClassDoc::methods)
			.flatMap(Arrays::stream)
			.map(ParserHelper::getMappingValue)
			.flatMap(Arrays::stream)
			.map(aValue -> (String) aValue.value())
			.map(this::createTag)
			.filter(Objects::nonNull)
			.forEach(tags::add);
	}

	/**
	 * This method creates the tag with the annotation value
	 */
	private Tag createTag(String annotationValue) {
		Matcher matcher = Parser.PATTERN_TAG_NAME.matcher(annotationValue);
		if (matcher.matches()) {
			String group = matcher.group(1);
			Tag tag = new Tag();
			tag.setName(group);
			return tag;
		}
		return null;
	}
}