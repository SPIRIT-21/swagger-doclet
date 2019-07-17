package com.spirit21.common.handler.property;

import java.util.Arrays;

import com.spirit21.common.Consts;
import com.spirit21.common.handler.datatype.DataTypeFactory;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.common.parser.AbstractParser;
import com.sun.javadoc.Type;

import io.swagger.models.properties.Property;
import io.swagger.models.properties.PropertyBuilder;

public class PropertyFactory {
	
	/**
	 * Creates a swagger property from a JavaDoc type. 
	 * Is used to set a property of a query parameter for example.
	 * 
	 * @param type The JavaDoc type out of which the swagger property will be created.
	 * @return The swagger property which represents the passed JavaDoc type.
	 */
	public static Property createSwaggerProperty(Type type) {
		String[] typeAndFormat = getTypeAndFormat(type);
	
		if (typeAndFormat != null) {
			PropertyHandler handler = getPropertyHandler(typeAndFormat);
			
			if (handler != null) {
				return handler.createProperty(type, typeAndFormat);
			}
			return PropertyBuilder.build(typeAndFormat[0], typeAndFormat[1], null);
		} else {
			String[] tmp = new String[2];
			tmp[0] = Consts.PROPERTY_TYPE_REF;
			
			CommonHelper.addToDefinitionList(type.asClassDoc());
			return createSwaggerProperty(type);
		}
	}
	
	/**
	 * Gets the correct property handler by a typeAndFormat array.
	 * 
	 * @param typeAndFormat Array which contains the swagger property type and format.
	 * @return The found PropertyHander by the typeAndFormat array or null.
	 */
	private static PropertyHandler getPropertyHandler(String[] typeAndFormat) {
		return Arrays.asList(PropertyHandler.values()).stream()
				.filter(propHandler -> typeAndFormat[0].equals(propHandler.getTypeName()))
				.findFirst()
				.orElse(null);
	}
	
	/**
	 * Returns a typeAndFormat array of a property in the following format:
	 * typeAndFormat[0] --> type
	 * typeAndFormat[1] --> format
	 * 
	 * @param type The type for which the typeAndFormat array will be created.
	 * @return An filled typeAndFormat array or an empty array if no type was recognized.
	 */
	private static String[] getTypeAndFormat(Type type) {
		if (AbstractParser.CLASS_DOC_CACHE.findByType(type) == null) {
			DataTypeFactory dataTypeFactory = new DataTypeFactory();
			return dataTypeFactory.getDataTypeAndFormat(type.qualifiedTypeName());
		}
		
		String[] typeAndFormat = new String[2];
		
		if (type.asClassDoc().isEnum()) {
			typeAndFormat[0] = Consts.PROPERTY_TYPE_ENUM;
		} else {
			typeAndFormat[0] = Consts.PROPERTY_TYPE_REF;
		}
		return typeAndFormat;
	}
}
