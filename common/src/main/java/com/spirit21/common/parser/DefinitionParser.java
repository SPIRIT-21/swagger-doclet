package com.spirit21.common.parser;

import java.util.HashMap;
import java.util.Map;

import com.spirit21.common.handler.property.PropertyFactory;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Swagger;
import io.swagger.models.properties.Property;

/**
 * Parses all ClassDocs which were added to the DEFINITION_CLASS_DOCS list during the conversion process.
 * From these ClassDocs swagger models are created.
 * 
 * @author mweidmann
 */
public class DefinitionParser {

	/**
	 * Creates for every ClassDoc in the DEFINITION_CLASS_DOCS list a swagger definition.
	 * Therefore a Map is created and for every ClassDoc a swagger Model will be created.
	 * Then the properties of the Model are set. Lastly, the Map is set into the Swagger object.
	 * NOTE: Implemented with a while loop due to insertion in the list during iteration.
	 * 
	 * @param swagger The Swagger object in which the definitions will be set.
	 */
	public void run(Swagger swagger) {
		Map<String, Model> swaggerDefinitions = new HashMap<>();
		ClassDoc classDoc = null;
		Integer index = 0;
		
		while (index < AbstractParser.DEFINITION_CLASS_DOCS.size()) {
			classDoc = AbstractParser.DEFINITION_CLASS_DOCS.get(index);
			
			ModelImpl modelImpl = new ModelImpl();
			modelImpl.setProperties(getProperties(classDoc));
			swaggerDefinitions.put(classDoc.name(), modelImpl);
			
			index++;
		}
		
		swagger.setDefinitions(swaggerDefinitions);
	}
	
	/**
	 * Creates out of a ClassDoc the swagger properties for a swagger model.
	 * Therefore a map is created and for every FieldDoc of the ClassDoc a swagger Property is created.
	 * Then the created swagger Property will be added to the map.
	 * 
	 * @param classDoc The ClassDoc for which the swagger properties should be created.
	 * @return The swagger properties in a map.
	 */
	private Map<String, Property> getProperties(ClassDoc classDoc) {
		Map<String, Property> properties = new HashMap<>();
		
		for (FieldDoc fieldDoc : classDoc.fields(false)) {
			Property property = PropertyFactory.createSwaggerProperty(fieldDoc.type());
			properties.put(fieldDoc.name(), property);
		}
		return properties;
	}
}
