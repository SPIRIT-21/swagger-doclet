package com.spirit21.common.handler.datatype;

import com.spirit21.common.Consts;

/** 
 * This handler handles strings
 */
public class StringHandler implements TypeHandler {
	
	/**
	 * This method creates the correct typeAndFormat array and returns it
	 */
	@Override
	public String[] getTypeAndFormat(String type) {
		if (type.equals(String.class.getName())) {
			String[] typeAndFormat = new String[2];
			typeAndFormat[0] = Consts.STRING;
			return typeAndFormat;
		}
		return null;
	}
}
