package com.spirit21.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.spirit21.handler.property.PropertyFactory;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Swagger;
import io.swagger.models.properties.Property;

public class DefinitionParser {

	/**
	 * This method creates the definitions map and the models 
	 * After that it sets it to the swagger object
	 */
	protected void setDefinitions(Swagger swagger) {
		Map<String, Model> definitions = new HashMap<>();

		for (int i = 0; i < Parser.definitionClassDocs.size(); i++) {
			ModelImpl model = new ModelImpl();
			model.setProperties(getProperties(i));
			definitions.put(Parser.definitionClassDocs.get(i).name(), model);
		}
		swagger.setDefinitions(definitions);
	}

	/**
	 * This method creates the properties map for a model
	 */
	private Map<String, Property> getProperties(int index) {
		Map<String, Property> properties = new HashMap<>();

		Arrays.asList(Parser.definitionClassDocs.get(index).fields(false))
			.forEach(fieldDoc -> {
				Property property = PropertyFactory.createProperty(fieldDoc.type());
				properties.put(fieldDoc.name(), property);
			});
		return properties;
	}
}