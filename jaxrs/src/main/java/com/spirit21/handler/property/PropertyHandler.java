package com.spirit21.handler.property;

import java.util.Arrays;

import com.spirit21.Consts;
import com.spirit21.helper.ParserHelper;
import com.spirit21.parser.Parser;
import com.sun.javadoc.Type;

import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.models.properties.StringProperty;

public enum PropertyHandler {
	
	REF(Consts.REF) {
		@Override
		public Property createProperty(String[] typeAndFormat, Type type) {
			RefProperty property = new RefProperty(type.simpleTypeName());
			ParserHelper.addToDefinitionList(Parser.classDocCache.findByType(type));
			return property;
		}
	},
	ARRAY(Consts.ARRAY) {
		@Override
		public Property createProperty(String[] typeAndFormat, Type type) {
			ArrayProperty property = new ArrayProperty();
			for (Type newType : type.asParameterizedType().typeArguments()) {
				property.setItems(PropertyFactory.createProperty(newType));
			}
			return property;
		}
	},
	MAP(Consts.MAP) {
		@Override
		public Property createProperty(String[] typeAndFormat, Type type) {
			MapProperty property = new MapProperty();
			property.additionalProperties(PropertyFactory.createProperty(type.asParameterizedType().typeArguments()[1]));
			return property;
		}
	},
	ENUM(Consts.ENUM) {
		@Override
		public Property createProperty(String[] typeAndFormat, Type type) {
			StringProperty property = new StringProperty();
			Arrays.asList(type.asClassDoc().enumConstants()).stream()
				.map(fieldDoc -> fieldDoc.name())
				.forEach(property::_enum);
			return property;
		}
	};
	
	private final String typeName;
	
	private PropertyHandler(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}
	
	public abstract Property createProperty(String[] typeAndFormat, Type type);
}
