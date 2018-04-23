package com.spirit21.handler.datatype;

/** 
 * This handler handles the type object
 */
public class ObjectHandler implements TypeHandler {
	
	/** 
	 * This method creates the correct typeAndFormat array and returns it
	 */
	@Override
	public String[] getTypeAndFormat(String type) {
		if (type.equals(Object.class.getName())) {
			String[] typeAndFormat = new String[2];
			typeAndFormat[0] = "object";
			return typeAndFormat;
		}
		return null;
	}
}