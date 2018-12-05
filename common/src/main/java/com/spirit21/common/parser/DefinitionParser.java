package com.spirit21.common.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.spirit21.common.handler.property.PropertyFactory;
import com.spirit21.common.helper.ClassDocCache;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;

import v2.io.swagger.models.Model;
import v2.io.swagger.models.ModelImpl;
import v2.io.swagger.models.Swagger;
import v2.io.swagger.models.properties.Property;

public class DefinitionParser {

	/**
	 * This method creates the definitions map and the models.
	 * After that it sets it to the swagger object
	 * NOTE: Did it with this for loop because only so you can add something
	 * 		 to a list, while looping over it 
	 */
	public void setDefinitions(Swagger swagger, List<ClassDoc> definitionClassDocs, ClassDocCache cache) {
		Map<String, Model> definitions = new HashMap<>();
	
		for (int i = 0; i < definitionClassDocs.size(); i++) {
			ModelImpl model = new ModelImpl();
			model.setProperties(getProperties(i, definitionClassDocs, cache));
			definitions.put(definitionClassDocs.get(i).name(), model);
		}
		swagger.setDefinitions(definitions);
	}
	
	/**
	 * This method creates the properties map for a model
	 */
	private Map<String, Property> getProperties(int index, List<ClassDoc> definitions, ClassDocCache cache) {
		Map<String, Property> properties = new HashMap<>();

		for (FieldDoc fieldDoc : definitions.get(index).fields(false)) {
			Property property = PropertyFactory.createProperty(fieldDoc.type(), definitions, cache);
			properties.put(fieldDoc.name(), property);
		}
		return properties;
	}
}
