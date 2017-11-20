package com.spirit21.parser;

import org.springframework.web.bind.annotation.RequestMapping;

import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.Swagger;
import io.swagger.models.Tag;

public class TagParser {

	protected void setTags(Swagger swagger) {
		for (ClassDoc classDoc : Parser.controllerClassDocs) {
			String annotationValue = ParserHelper.getPathOrValueOfAnnotation(classDoc, RequestMapping.class.getName());
			if (annotationValue != null) {
				Tag tag = new Tag();
				tag.setName(annotationValue);
				swagger.addTag(tag);
			} else {
				for (MethodDoc methodDoc : classDoc.methods()) {
					String s = ParserHelper.getFullPath(methodDoc);
					if (!doesTagExist(swagger, s)) {
						Tag tag = new Tag();
						tag.setName(s);
						swagger.addTag(tag);
					}
				}
			}
		}
	}

	private boolean doesTagExist(Swagger swagger, String tagName) {
		for (Tag tag : swagger.getTags()) {
			if (tag.getName().equals(tagName)) {
				return true;
			}
		}
		return false;
	}
}