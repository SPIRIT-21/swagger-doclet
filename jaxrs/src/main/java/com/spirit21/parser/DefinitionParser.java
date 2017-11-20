package com.spirit21.parser;

import java.util.HashMap;
import java.util.Map;

import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.FieldDoc;

import io.swagger.models.Model;
import io.swagger.models.ModelImpl;
import io.swagger.models.Swagger;
import io.swagger.models.properties.Property;

public class DefinitionParser {

	protected void setDefinitions(Swagger swagger) {
		Map<String, Model> definitions = new HashMap<>();
		
//		ListIterator<ClassDoc> listIterator = Parser.definitionClassDocs.listIterator();
//		while (listIterator.hasNext()) {
//			ClassDoc classDoc = (ClassDoc) listIterator.next();
//			listIterator.
//		}
		
		for (int i = 0; i < Parser.definitionClassDocs.size(); i++) {
			ModelImpl model = new ModelImpl();
			Map<String, Property> properties = new HashMap<>();
			for (FieldDoc fieldDoc : Parser.definitionClassDocs.get(i).fields(false)) {
				String[] typeAndFormat = ParserHelper.checkTypeAndFormat(fieldDoc.type());
				Property property = ParserHelper.createProperty(typeAndFormat, fieldDoc.type());
				properties.put(fieldDoc.name(), property);
			}
			model.setProperties(properties);
			definitions.put(Parser.definitionClassDocs.get(i).name(), model);
		}
		swagger.setDefinitions(definitions);
	}
}