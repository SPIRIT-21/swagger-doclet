package com.spirit21.common.handler.property;

import java.util.Arrays;
import java.util.List;

import com.spirit21.common.Consts;
import com.spirit21.common.helper.ClassDocCache;
import com.spirit21.common.helper.CommonHelper;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.Type;

import v2.io.swagger.models.properties.ArrayProperty;
import v2.io.swagger.models.properties.MapProperty;
import v2.io.swagger.models.properties.Property;
import v2.io.swagger.models.properties.RefProperty;
import v2.io.swagger.models.properties.StringProperty;

/**
 * This enum handles all possible properties
 */
public enum PropertyHandler {

	REF(Consts.REF) {
		@Override
		public Property createProperty(String[] typeAndFormat, Type type, ClassDocCache cache,
				List<ClassDoc> definitions) {
			RefProperty property = new RefProperty(type.simpleTypeName());
			CommonHelper.addToDefinitionList(definitions, cache.findByType(type));
			return property;
		}
	},
	ARRAY(Consts.ARRAY) {
		@Override
		public Property createProperty(String[] typeAndFormat, Type type, ClassDocCache cache,
				List<ClassDoc> definitions) {
			ArrayProperty property = new ArrayProperty();

			for (Type newType : type.asParameterizedType().typeArguments()) {
				property.setItems(PropertyFactory.createProperty(newType, definitions, cache));
			}
			return property;
		}
	},
	MAP(Consts.MAP) {
		@Override
		public Property createProperty(String[] typeAndFormat, Type type, ClassDocCache cache,
				List<ClassDoc> definitions) {
			MapProperty property = new MapProperty();

			Type genericOfMap = type.asParameterizedType().typeArguments()[1];
			Property prop = PropertyFactory.createProperty(genericOfMap, definitions, cache);

			property.additionalProperties(prop);
			return property;
		}
	},
	ENUM(Consts.ENUM) {
		@Override
		public Property createProperty(String[] typeAndFormat, Type type, ClassDocCache cache,
				List<ClassDoc> definitions) {
			StringProperty property = new StringProperty();

			Arrays.asList(type.asClassDoc().enumConstants()).stream()
				.map(FieldDoc::name)
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

	public abstract Property createProperty(String[] typeAndFormat, Type type, ClassDocCache cache,
			List<ClassDoc> definitions);
}
