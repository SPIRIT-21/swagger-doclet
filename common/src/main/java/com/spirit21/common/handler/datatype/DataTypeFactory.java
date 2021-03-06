package com.spirit21.common.handler.datatype;

import java.util.ArrayList;
import java.util.List;

public class DataTypeFactory {

	private List<TypeHandler> handlers;
	
	/** 
	 * The constructor adds all handlers in one list
	 */
	public DataTypeFactory() {
		handlers = new ArrayList<>();
		handlers.add(new CollectionHandler());
		handlers.add(new BooleanHandler());
		handlers.add(new DateHandler());
		handlers.add(new NumberHandler());
		handlers.add(new ObjectHandler());
		handlers.add(new StringHandler());
	}
	
	/**
	 * Tries all handlers on a given type name and returns a typeAndFormat array
	 */
	public String[] getDataType(String typeName) {
		String[] typeAndFormat = null;
		
		for (TypeHandler typeHandler : handlers) {
			typeAndFormat = typeHandler.getTypeAndFormat(typeName);
			
			if (typeAndFormat != null) {
				break;
			}
		}
		return typeAndFormat;
	}
}
