package com.spirit21.common.handler.datatype;

/**
 * Interface which will be implemented in every enum of every handler which contains type information.
 * 
 * @author mweidmann
 */
public interface ITypeEnum {
	
	/**
	 * Returns the type name of a specific type.
	 * 
	 * @return The type name to a type.
	 */
	public abstract String getTypeName();

	/**
	 * Gets an string array with two elements.
	 * The first element is the type (in swagger form), e.g. number, boolean.
	 * The second element is the format (in swagger form), e.g. integer, float.
	 * If the format is not necessary the second element will be null.
	 * 
	 * @return The string array with some content.
	 */
	public abstract String[] getTypeAndFormat();
}
