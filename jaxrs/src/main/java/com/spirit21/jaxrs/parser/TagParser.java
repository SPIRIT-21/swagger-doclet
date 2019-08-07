package com.spirit21.jaxrs.parser;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import javax.ws.rs.Path;

import com.spirit21.common.CommonConsts;
import com.spirit21.common.helper.CommonHelper;

import io.swagger.models.Swagger;
import io.swagger.models.Tag;

public class TagParser {
	
	/**
	 * This method sets all tags into the swagger model
	 */
	protected void setTags(Swagger swagger) {
		Set<Tag> tags = Parser.resourceClassDocs.entrySet().stream()
			.filter(entry -> entry.getValue() == null)
			.map(entry -> CommonHelper.getAnnotationValue(entry.getKey(), Path.class.getName(), CommonConsts.ANNOTATION_PROPERTY_NAME_VALUE))
			.filter(Objects::nonNull)
			.map(aValue -> (String) CommonHelper.getAnnotationValueObject(aValue))
			.filter(Objects::nonNull)
			.map(this::createTag)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
		
		swagger.setTags(new ArrayList<>(tags));
	}
	
	/**
	 * This method creates the tag with the annotation value 
	 */
	private Tag createTag(String annotationValue) {
		Matcher matcher = Parser.TAG_NAME_PATTERN.matcher(annotationValue);
		if (matcher.matches()) {
			String group = matcher.group(1);
			Tag tag = new Tag();
			tag.setName(group);
			return tag;
		}
		return null;
	}
}