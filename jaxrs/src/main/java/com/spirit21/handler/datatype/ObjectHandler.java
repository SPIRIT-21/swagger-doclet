package com.spirit21.handler.datatype;

// This handler handles all types of Objects...
public class ObjectHandler implements TypeHandler {
	
	// This method creates the correct typeAndFormat array and returns it
	@Override
	public String[] getTypeAndFormat(String type) {
		String[] typeAndFormat = new String[2];
		if (type.equals(Object.class.getName())) {
			typeAndFormat[0] = "object";
			return typeAndFormat;
		}
		return null;
	}
}