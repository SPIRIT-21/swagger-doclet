package com.spirit21.common.handler.datatype;

import java.util.List;
import java.util.Objects;

/**
 * A factory which returns the correct type and format (in swagger form) for a passed type.
 * 
 * @author mweidmann
 */
public class DataTypeFactory {

	private List<AbstractTypeHandler<?>> handlers;

	/**
	 * The constructor adds all handlers in one list.
	 */
	public DataTypeFactory() {
		handlers.add(new CollectionTypeHandler());
		handlers.add(new DateTypeHandler());
		handlers.add(new NumberTypeHandler());
		handlers.add(new ObjectTypeHandler());
		handlers.add(new PrimitiveTypeHandler());
		handlers.add(new StringTypeHandler());
	}

	/**
	 * Iterates over all type handlers. For each handler the getTypeAndFormat
	 * function is called. If the array is not empty, the correct typeAndFormat
	 * array was found.
	 * 
	 * @param typeName The name of the type.
	 * @return The correctly filled typeAndFormat array or an empty array.
	 */
	public String[] getDataType(String typeName) {
		return handlers.stream()
				.map(typeHandler -> typeHandler.getTypeAndFormat(typeName))
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(new String[0]);
	}
}
