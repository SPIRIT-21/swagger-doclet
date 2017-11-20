package com.spirit21.handler.datatype;

import java.util.ArrayList;
import java.util.List;

public class DataTypeFactory {

	private List<TypeHandler> handlers;
	
	// The constructor adds all handlers in one list
	public DataTypeFactory() {
		handlers = new ArrayList<>();
		handlers.add(new ArrayHandler());
		handlers.add(new BooleanHandler());
		handlers.add(new DateHandler());
		handlers.add(new NumberHandler());
		handlers.add(new ObjectHandler());
		handlers.add(new StringHandler());
	}
	
	// Tries all handlers on a given typeName and returns a typeAndFormat array
	public String[] getDataType(String name) {
		String[] typeAndFormat = null;
		// Iterate through all handlers
		for (TypeHandler typeHandler : handlers) {
			// Get the typeAndFormat array and check if it is filled
			typeAndFormat = typeHandler.getTypeAndFormat(name);
			if (typeAndFormat != null) {
				break;
			}
		}
		return typeAndFormat;
	}
}
