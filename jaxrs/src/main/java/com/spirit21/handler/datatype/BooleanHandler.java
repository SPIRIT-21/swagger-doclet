package com.spirit21.handler.datatype;

// This handler handles all types of Bools...
public class BooleanHandler implements TypeHandler {
	
	// This method creates the correct typeAndFormat array and returns it
	@Override
	public String[] getTypeAndFormat(String type) {
		String[] typeAndFormat = new String[2];
		if (type.equals(boolean.class.getName()) || type.equals(Boolean.class.getName())) {
			typeAndFormat[0] = "boolean";
			return typeAndFormat;
		}
		return null;
	}
}
