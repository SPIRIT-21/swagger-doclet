package com.spirit21.parser;

import com.spirit21.Consts;
import com.spirit21.handler.javadoc.ApiTagHandler;
import com.sun.javadoc.Tag;

import io.swagger.models.Info;
import io.swagger.models.Swagger;

public class ApiParser {

	private String fileName;

	public void setBasicInformation(Swagger swagger) {
		Info info = new Info();
		for (Tag tag : Parser.entryPointClassDoc.tags()) {
			if (tag.name().equals(Consts.FILE_NAME)) {
				fileName = tag.text();
				continue;
			}
			for (ApiTagHandler ath : ApiTagHandler.values()) {
				if (ath.getName().equals(tag.name())) {
					ath.setValue(swagger, info, tag.text());
					break;
				}
			}
		}
		try {
			checkInfo(info);
		} catch (Exception e) {
			// EXCEPTION
		}
		swagger.setInfo(info);
	}

	private void checkInfo(Info info) {
		if (info.getVersion().isEmpty() || info.getVersion() == null) {
			// EXCEPTION
		} else if (info.getTitle().isEmpty() || info.getTitle() == null) {
			// EXCEPTION
		}
	}

	public String getFileName() {
		return fileName;
	}
}