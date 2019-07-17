package com.spirit21.common.handler.property;

import java.util.Arrays;

import com.spirit21.common.Consts;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.common.parser.AbstractParser;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.Type;

import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

/**
 * Handles all possible swagger property types.
 * 
 * @author mweidmann
 */
public enum PropertyHandler {
	
	/**
	 * Handles ref properties by creating a RefProperty and add it to the Parser.defintionClassDocs list.
	 * 
	 * @author mweidmann
	 */
	REF_PROPERTY(Consts.PROPERTY_TYPE_REF) {
		@Override
		public Property createProperty(Type type, String[] typeAndFormat) {
			RefProperty refProperty = new RefProperty(type.simpleTypeName());
			CommonHelper.addToDefinitionList(AbstractParser.CLASS_DOC_CACHE.findByType(type));
			return refProperty;
		}
	},
	/**
	 * Handles array properties by creating an ArrayProperty. Then the type of the array will be set by creating a swagger property.
	 * 
	 * @author mweidmann
	 */
	ARRAY_PROPERTY(Consts.PROPERTY_TYPE_ARRAY) {
		@Override
		public Property createProperty(Type type, String[] typeAndFormat) {
			ArrayProperty arrayProperty = new ArrayProperty();
			
			Arrays.asList(type.asParameterizedType().typeArguments()).stream()
				.map(PropertyFactory::createSwaggerProperty)
				.forEach(arrayProperty::setItems);
			
			return arrayProperty;
		}
	},
	/**
	 * Handles map properties by creating a MapProperty. After that a property of the value of the map will be created.
	 * The created generic property will be set in the MapProperty.
	 * 
	 * @author mweidmann
	 */
	MAP_PROPERTY(Consts.PROPERTY_TYPE_MAP) {
		@Override
		public Property createProperty(Type type, String[] typeAndFormat) {
			MapProperty mapProperty = new MapProperty();

			// Hardcoded array access at 1 because this gets the type of the Map value.
			Type valueOfMap = type.asParameterizedType().typeArguments()[1];
			Property propertyValueOfMap = PropertyFactory.createSwaggerProperty(valueOfMap);

			mapProperty.additionalProperties(propertyValueOfMap);
			return mapProperty;
		}
	},
	/**
	 * Handle enum properties by creating a StringProperty.
	 * By iterating over the enum constants all of them will be set to the StringProperty.
	 * 
	 * @author mweidmann
	 */
	ENUM_PROPERTY(Consts.PROPERTY_TYPE_ENUM) {
		@Override
		public Property createProperty(Type type, String[] typeAndFormat) {
			StringProperty stringProperty = new StringProperty();

			Arrays.asList(type.asClassDoc().enumConstants()).stream()
				.map(FieldDoc::name)
				.forEach(stringProperty::_enum);

			return stringProperty;
		}
	};

	private final String typeName;

	private PropertyHandler(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}
	
	/**
	 * Creates a swagger property out of a JavaDoc type and the typeAndFormat array.
	 * 
	 * @param type The JavaDoc Type which should be converted to a swagger property.
	 * @param typeAndFormat Contains information which type and format the JavaDoc type has.
	 * @return The created swagger property.
	 */
	public abstract Property createProperty(Type type, String[] typeAndFormat);
}
