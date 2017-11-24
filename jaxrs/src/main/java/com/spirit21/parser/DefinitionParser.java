package com.spirit21.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.spirit21.helper.ParserHelper;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Swagger;
import io.swagger.models.properties.Property;

public class DefinitionParser {

	/**
	 * This method creates the definitions Map and sets it to the swagger object
	 */
	protected void setDefinitions(Swagger swagger) {
		Map<String, Model> definitions = new HashMap<>();
		
		// Iterate through the definitionClassDoc list
		for (int i = 0; i < Parser.definitionClassDocs.size(); i++) {
			// Create a new model
			ModelImpl model = new ModelImpl();
			// set the properties 
			model.setProperties(getProperties(i));
			// put it in the map
			definitions.put(Parser.definitionClassDocs.get(i).name(), model);
		}

		swagger.setDefinitions(definitions);
	}
	
	/**
	 * This method creates the properties map for a model
	 */
	private Map<String, Property> getProperties(int index) {
		Map<String, Property> properties = new HashMap<>();
		
		// Iterate over fields
		Arrays.asList(Parser.definitionClassDocs.get(index).fields(false))
			.forEach(f -> {
				// Create property and put it in properties map
				String[] typeAndFormat = ParserHelper.checkTypeAndFormat(f.type());
				Property property = ParserHelper.createProperty(typeAndFormat, f.type());
				properties.put(f.name(), property);
			});
		
		return properties;
	}
}