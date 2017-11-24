package com.spirit21.parser;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ws.rs.Path;

import com.spirit21.helper.ParserHelper;

import io.swagger.models.Swagger;
import io.swagger.models.Tag;

public class TagParser {
	
	private Pattern pattern;
	
	protected TagParser() {
		pattern = Pattern.compile("\"/?([a-zA-Z0-9_-]+)/?.*\"");
	}
	
	/**
	 * This method sets all tags into the swagger object
	 */
	protected void setTags(Swagger swagger) {
		Set<Tag> hashSet = Parser.resourceClassDocs.entrySet().stream()
			.filter(e -> e.getValue() == null)
			.map(e -> ParserHelper.getAnnotationValue(e.getKey(), Path.class.getName()))
			.filter(Objects::nonNull)
			.map(this::createTag)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
		swagger.setTags(new ArrayList<>(hashSet));	
	}
	
	/**
	 * This method creates the tag with the annotationValue 
	 */
	private Tag createTag(String annotationValue) {
		Matcher matcher = pattern.matcher(annotationValue);
		if (matcher.matches()) {
			String group = matcher.group(1);
			Tag tag = new Tag();
			tag.setName(group);
			return tag;
		}
		return null;
	}
}