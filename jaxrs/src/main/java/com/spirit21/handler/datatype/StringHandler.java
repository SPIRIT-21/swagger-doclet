package com.spirit21.handler.datatype;

// This handler handles all types of Strings/Character...
public class StringHandler implements TypeHandler {
	
	// This method creates the correct typeAndFormat Array and returns it
	@Override
	public String[] getTypeAndFormat(String type) {
		String[] typeAndFormat = new String[2];
		if (type.equals(String.class.getName())) {
			typeAndFormat[0] = "string";
			return typeAndFormat;
		} else if (type.equals(char.class.getName()) || type.equals(Character.class.getName())) {
			typeAndFormat[0] = "string";
			typeAndFormat[1] = "char";
		}
		return null;
	}
}