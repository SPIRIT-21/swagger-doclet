package com.spirit21.handler.datatype;

/** 
 * This handler handles all types of Strings/Character...
 */
public class StringHandler implements TypeHandler {
	
	/**
	 * This method creates the correct typeAndFormat Array and returns it
	 */
	@Override
	public String[] getTypeAndFormat(String type) {
		if (type.equals(String.class.getName())) {
			String[] typeAndFormat = new String[2];
			typeAndFormat[0] = "string";
			return typeAndFormat;
		}
		return null;
	}
}