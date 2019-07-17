package com.spirit21.common.handler.datatype;

import java.util.Arrays;

/**
 * Abstract class from which every handler will inherit.
 * 
 * @author mweidmann
 *
 * @param <T> The type enum as a generic parameter.
 */
public abstract class AbstractTypeHandler<T extends ITypeEnum> {
	
	private T[] enumValues;
	
	public AbstractTypeHandler(Class<T> enumClass) {
		this.enumValues = enumClass.getEnumConstants();
	}
	
	/**
	 * Iterates over all enum values and filters if a value matches with the passed typeName.
	 * If it matches it returns the typeAndFormat array. If not a empty string array will be returned.
	 * 
	 * @param typeName The name of the type.
	 * @return A with content filled typeAndFormat array or an empty array.
	 */
	public String[] getTypeAndFormat(String typeName) {
		return Arrays.asList(enumValues).stream()
				.filter(enumValue -> typeName.equals(enumValue.getTypeName()))
				.map(T::getTypeAndFormat)
				.findFirst()
				.orElse(new String[0]);
	}
}
