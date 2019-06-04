package com.spirit21.common.handler.datatype;

import com.spirit21.common.Consts;
import com.spirit21.common.handler.datatype.StringTypeHandler.StringType;

/**
 * This handler handles Strings.
 * 
 * @author mweidmann
 */
public class StringTypeHandler extends AbstractTypeHandler<StringType> {
	
	public StringTypeHandler() {
		super(StringType.class);
	}

	/**
	 * Enum which contains the String type.
	 * 
	 * @author mweidmann
	 */
	public enum StringType implements ITypeEnum {
		STRING(String.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.STRING, null };
			}
		};
		
		private final String typeName;
		
		private StringType(String typeName) {
			this.typeName = typeName;
		}
		
		@Override
		public String getTypeName() {
			return typeName;
		}
	}
}
