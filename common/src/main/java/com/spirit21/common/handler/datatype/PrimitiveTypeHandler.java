package com.spirit21.common.handler.datatype;

import java.util.Arrays;

import com.spirit21.common.CommonConsts;
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
				return new String[] { CommonConsts.DATA_TYPE_NUMBER, getPrimitiveName() };
			}
		},
		SHORT(short.class.getName(), Short.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { CommonConsts.DATA_TYPE_NUMBER, getPrimitiveName() };
			}
		},
		INTEGER(int.class.getName(), Integer.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { CommonConsts.DATA_TYPE_INTEGER, null };
			}
		},
		LONG(long.class.getName(), Long.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { CommonConsts.DATA_TYPE_INTEGER, getPrimitiveName() };
			}
		},
		FLOAT(float.class.getName(), Float.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { CommonConsts.DATA_TYPE_NUMBER, getPrimitiveName() };
			}
		},
		DOUBLE(double.class.getName(), Double.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { CommonConsts.DATA_TYPE_NUMBER, getPrimitiveName() };
			}
		},
		BOOLEAN(boolean.class.getName(), Boolean.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { CommonConsts.DATA_TYPE_BOOLEAN, null };
			}
		},
		CHAR(char.class.getName(), Character.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { CommonConsts.DATA_TYPE_NUMBER, getPrimitiveName() };
			}
		};
		
		private final String primitiveName;
		private final String objectName;

		private PrimitiveTypes(String primitiveName, String objectName) {
			this.primitiveName = primitiveName;
			this.objectName = objectName;
		}

		public String getPrimitiveName() {
			return primitiveName;
		}
		
		public String getObjectName() {
			return objectName;
		}

		@Override
		public String getTypeName() {
			return "";
		}
	}
	
	@Override
	public String[] getTypeAndFormat(String typeName) {
		return Arrays.asList(PrimitiveTypes.values()).stream()
				.filter(enumValue -> typeName.equals(enumValue.getObjectName()) || typeName.equals(enumValue.getPrimitiveName()))
				.map(PrimitiveTypes::getTypeAndFormat)
				.findFirst()
				.orElse(new String[0]);
	}
}
