package com.spirit21.common.handler.datatype;

import com.spirit21.common.Consts;

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
			typeAndFormat[0] = Consts.OBJECT;
			return typeAndFormat;
		}
		return null;
	}
}
