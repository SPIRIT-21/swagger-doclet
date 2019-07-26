package com.spirit21.common.handler.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.spirit21.common.CommonConsts;
import com.spirit21.common.handler.datatype.NumberTypeHandler.NumberTypes;

/**
 * This handler handles the most common numeric types.
 * 
 * @author mweidmann
 */
public class NumberTypeHandler extends AbstractTypeHandler<NumberTypes> {

	public NumberTypeHandler() {
		super(NumberTypes.class);
	}

	/**
	 * Enum which contains the most common numeric types.
	 * 
	 * @author mweidmann
	 */
	public enum NumberTypes implements ITypeEnum {
		BIG_INTEGER(BigInteger.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { CommonConsts.DATA_TYPE_NUMBER, null };
			}
		},
		BIG_DECIMAL(BigDecimal.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { CommonConsts.DATA_TYPE_NUMBER, null };
			}
		};

		private final String typeName;
		
		private NumberTypes(String typeName) {
			this.typeName = typeName;
		}
		
		@Override
		public String getTypeName() {
			return typeName;
		}
	}
}
