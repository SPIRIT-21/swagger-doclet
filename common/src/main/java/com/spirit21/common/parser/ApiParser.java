package com.spirit21.common.parser;

import java.util.Arrays;

import com.spirit21.common.Consts;
import com.spirit21.common.exception.ApiParserException;
import com.spirit21.common.handler.javadoc.ApiTagHandler;

import io.swagger.models.Info;
import io.swagger.models.Swagger;

/**
 * Parses the entry point ClassDoc for basic API information. 
 * The needed information should be located in the JavaDoc of the class.
 * 
 * @author mweidmann
 */
public class ApiParser {

	private String fileName;

	/**
	 * Sets the basic API information into the Swagger object.
	 * Then the set information is checked for correctness.
	 * 
	 * @param swagger The Swagger object in which the basic API information will be set.
	 * @throws ApiParserException if required information is missing.
	 */
	public void run(Swagger swagger) throws ApiParserException {
		Info info = new Info();
		
		Arrays.asList(AbstractParser.entryPointClassDoc.tags())
			.forEach(tag -> setBasicInformation(swagger, info, tag.name(), tag.text()));
		
		try {
			checkInfoObject(info);
		} catch (ApiParserException e) {
			throw new ApiParserException("You need to provide general information about your API.", e);
		}
		
		swagger.setInfo(info);
	}
	
	/**
	 * The information is actually set here. Generally, the ApiTagHandler handles every tag except
	 * of the file name tag. The reason is that the file name is not a relevant information for the
	 * content of the swagger file.
	 * 
	 * @param swagger The Swagger object in which some tag values will be saved.
	 * @param info The Info object in which some tag values will be saved.
	 * @param tagName The name of the used tag. For example: "@apiDescription".
	 * @param tagText The value of the used tag. For example: "A little description of the API".
	 */
	private void setBasicInformation(Swagger swagger, Info info, String tagName, String tagText) {
		if (tagName.equals(Consts.API_PARSER_FILE_NAME)) {
			if (tagText != null && !tagText.isEmpty()) {
				fileName = tagText;
			} else {
				fileName = Consts.DEFAULT_FILE_NAME;
			}
		}
		
		Arrays.asList(ApiTagHandler.values()).stream()
			.filter(ath -> ath.getTagName().equals(tagName))
			.forEach(ath -> ath.setTagValueToSwaggerModel(swagger, info, tagText));
	}

	/**
	 * Checks if the Info object contains a version and a title because these are required information for a
	 * valid swagger file.
	 * 
	 * @param info The Info object which should be checked.
	 * @throws ApiParserException If required information is missing.
	 */
	private void checkInfoObject(Info info) throws ApiParserException {
		if (info.getVersion() == null || info.getVersion().isEmpty()) {
			throw new ApiParserException("You need to provide general information about your API. Version is required.");
		} else if (info.getTitle() == null || info.getTitle().isEmpty()) {
			throw new ApiParserException("You need to provide general information about your API. Title is required.");
		}
	}

	public String getFileName() {
		return fileName;
	}
}
