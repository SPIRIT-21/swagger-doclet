package com.spirit21.handler.property;

import com.spirit21.Consts;
import com.spirit21.handler.datatype.DataTypeFactory;
import com.spirit21.helper.ParserHelper;
import com.spirit21.parser.Parser;
import com.sun.javadoc.Type;

import io.swagger.models.properties.Property;
import io.swagger.models.properties.PropertyBuilder;

public class PropertyFactory {
	
	/**
	 * This method creates a property from a type
	 */
	public static Property createProperty(Type type) {
		String[] typeAndFormat = getTypeAndFormat(type); 
		
		if (typeAndFormat != null) {
			PropertyHandler handler = getHandler(typeAndFormat);
			
			if (handler != null) {
				return handler.createProperty(typeAndFormat, type);
			}
			return PropertyBuilder.build(typeAndFormat[0], typeAndFormat[1], null);

		} else {
			String[] temp = new String[2];
			temp[0] = Consts.REF;
			ParserHelper.addToDefinitionList(type.asClassDoc());
			return createProperty(type);
		}
	}
	
	/**
	 * This method gets the correct property handler for the type
	 */
	private static PropertyHandler getHandler(String[] typeAndFormat) {
		for (PropertyHandler handler : PropertyHandler.values()) {
			if (typeAndFormat[0].equals(handler.getTypeName())) {
				return handler;
			}
		}
		return null;
	}
	
	/**
	 * This method returns the type and format of a property in this format
	 * String[0] = type, String[1] = format
	 */
	private static String[] getTypeAndFormat(Type type) {
		String[] typeAndFormat = null;
		
		if (Parser.classDocCache.findByType(type) != null) {
			typeAndFormat = new String[2];
			
			if (type.asClassDoc().isEnum()) {
				typeAndFormat[0] = Consts.ENUM;
			} else {
				typeAndFormat[0] = Consts.REF;
			}
			return typeAndFormat;
		} else {
			DataTypeFactory dataTypeFactory = new DataTypeFactory();
			typeAndFormat = dataTypeFactory.getDataType(type.qualifiedTypeName());
			return typeAndFormat;
		}
	}
}
