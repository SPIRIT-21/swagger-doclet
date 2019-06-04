package com.spirit21.common.handler.datatype;

import java.util.Arrays;

import com.spirit21.common.Consts;
import com.spirit21.common.handler.datatype.PrimitiveTypeHandler.PrimitiveTypes;

/**
 * This handler handles primitive types and their corresponding Objects.
 * 
 * @author mweidmann
 */
public class PrimitiveTypeHandler extends AbstractTypeHandler<PrimitiveTypes> {

	public PrimitiveTypeHandler() {
		super(PrimitiveTypes.class);
	}

	/**
	 * Enum which contains primitive types and their corresponding Objects.
	 * 
	 * @author mweidmann
	 */
	public enum PrimitiveTypes implements ITypeEnum {
		BYTE(byte.class.getName(), Byte.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.NUMBER, getPrimitiveName() };
			}
		},
		SHORT(short.class.getName(), Short.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.NUMBER, getPrimitiveName() };
			}
		},
		INTEGER(int.class.getName(), Integer.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.INTEGER, null };
			}
		},
		LONG(long.class.getName(), Long.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.INTEGER, getPrimitiveName() };
			}
		},
		FLOAT(float.class.getName(), Float.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.NUMBER, getPrimitiveName() };
			}
		},
		DOUBLE(double.class.getName(), Double.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.NUMBER, getPrimitiveName() };
			}
		},
		BOOLEAN(boolean.class.getName(), Boolean.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { "boolean", null };
			}
		},
		CHAR(char.class.getName(), Character.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.NUMBER, getPrimitiveName() };
			}
		};
		
		private final String primitiveName;
		private final String typeName;

		private PrimitiveTypes(String primitiveName, String typeName) {
			this.primitiveName = primitiveName;
			this.typeName = typeName;
		}

		public String getPrimitiveName() {
			return primitiveName;
		}

		@Override
		public String getTypeName() {
			return typeName;
		}
	}
	
	@Override
	public String[] getTypeAndFormat(String typeName) {
		return Arrays.asList(PrimitiveTypes.values()).stream()
				.filter(enumValue -> typeName.equals(enumValue.getTypeName()) || typeName.equals(enumValue.getPrimitiveName()))
				.map(PrimitiveTypes::getTypeAndFormat)
				.findFirst()
				.orElse(new String[0]);
	}
}
