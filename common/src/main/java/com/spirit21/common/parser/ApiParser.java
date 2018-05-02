package com.spirit21.common.parser;

import java.util.Arrays;

import com.spirit21.common.Consts;
import com.spirit21.common.exception.ApiParserException;
import com.spirit21.common.handler.javadoc.ApiTagHandler;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Tag;

import io.swagger.models.Info;
import io.swagger.models.Swagger;

public class ApiParser {

	private String fileName;

	/**
	 * This method sets the basic information for the swagger file,
	 * checks the info and throws possibly an exception
	 */
	public void setBasicInformation(Swagger swagger, ClassDoc entryPointClassDoc) throws ApiParserException {
		Info info = new Info();
		
		Arrays.asList(entryPointClassDoc.tags())
			.forEach(tag -> setInformation(swagger, info, tag));
		
		try {
			checkInfo(info);
		} catch (ApiParserException e) {
			throw new ApiParserException("You need to provide general information about your API!", e);
		}
		
		swagger.setInfo(info);
	}
	
	/**
	 * This method helps the method setBasicInformation to get the relevant information
	 */
	private void setInformation(Swagger swagger, Info info, Tag tag) {
		if (tag.name().equals(Consts.FILE_NAME)) {
			fileName = tag.text();
			return;
		}
		
		Arrays.asList(ApiTagHandler.values()).stream()
			.filter(ath -> ath.getName().equals(tag.name()))
			.forEach(ath -> ath.setValue(swagger, info, tag.text()));
	}

	/**
	 * This method checks the info object
	 */
	private void checkInfo(Info info) throws ApiParserException {
		if (info.getVersion() == null || info.getVersion().isEmpty()) {
			throw new ApiParserException("You need to provide general information about your API! Version is required.");
		} else if (info.getTitle() == null || info.getTitle().isEmpty()) {
			throw new ApiParserException("You need to provide general information about your API! Title is required.");
		}
	}

	public String getFileName() {
		return fileName;
	}
}
