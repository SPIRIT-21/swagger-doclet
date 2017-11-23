package com.spirit21.parser;

import java.util.Arrays;

import com.spirit21.Consts;
import com.spirit21.exception.ApiParserException;
import com.spirit21.handler.javadoc.ApiTagHandler;
import com.sun.javadoc.Tag;

import io.swagger.models.Info;
import io.swagger.models.Swagger;

public class ApiParser {

	private String fileName;
	
	/* 
	 * This method sets the basic information for the swagger file,
	 * checks the info and throws possibly an exception
	 */
	protected void setBasicInformation(Swagger swagger) throws ApiParserException {
		Info info = new Info();
		Arrays.asList(Parser.entryPointClassDoc.tags())
			.forEach(t -> setInformation(swagger, info, t));
		try {
			checkInfo(info);
		} catch (ApiParserException e) {
			throw new ApiParserException("You need to provide general information to your API!", e);
		}
		swagger.setInfo(info);
	}
	
	// This method helps the method 'setBasicInformation'
	private void setInformation(Swagger swagger, Info info, Tag tag) {
		if (tag.name().equals(Consts.FILE_NAME)) {
			fileName = tag.text();
		}
		Arrays.asList(ApiTagHandler.values()).stream()
			.filter(ath -> ath.getName().equals(tag.name()))
			.forEach(ath -> ath.setValue(swagger, info, tag.text()));
	}
	
	// This method checks the info
	private void checkInfo(Info info) throws ApiParserException {
		if (info.getVersion() == null || info.getVersion().isEmpty()) {
			throw new ApiParserException("You need to provide information about your API! Version is required.");
		} else if (info.getTitle() == null || info.getTitle().isEmpty()) {
			throw new ApiParserException("You need to provide information about your API! Title is required.");
		}
	}

	protected String getFileName() {
		return fileName;
	}
}