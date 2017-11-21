package com.spirit21.handler.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.spirit21.Consts;

//This handler handles all types of number...
public class NumberHandler implements TypeHandler {
	
	// This enum saves the most common number types e.g. Integer, Long...
	private enum NumberEnum {
		BYTE(byte.class.getName(), Byte.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.NUMBER;
				typeAndFormat[1] = getPrimitive();
				return typeAndFormat;
			}
		},
		SHORT(short.class.getName(), Short.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.NUMBER;
				typeAndFormat[1] = getPrimitive();
				return typeAndFormat;
			}
		},
		INTEGER(int.class.getName(), Integer.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.INTEGER;
				return typeAndFormat;
			}
		},
		LONG(long.class.getName(), Long.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.INTEGER;
				typeAndFormat[1] = getPrimitive();
				return typeAndFormat;
			}
		},
		FLOAT(float.class.getName(), Float.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.NUMBER;
				typeAndFormat[1] = getPrimitive();
				return typeAndFormat;
			}
		},
		DOUBLE(double.class.getName(), Double.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.NUMBER;
				typeAndFormat[1] = getPrimitive();
				return typeAndFormat;
			}
		},
		BIG_INTEGER(null, BigInteger.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.NUMBER;
				return typeAndFormat;
			}
		},
		BIG_DECIMAL(null, BigDecimal.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.NUMBER;
				return typeAndFormat;
			}
		};

		private final String primitive;
		private final String objectOfPrimitive;

		private NumberEnum(String primitive, String objectOfPrimitive) {
			this.primitive = primitive;
			this.objectOfPrimitive = objectOfPrimitive;
		}

		public String getPrimitive() {
			return primitive;
		}

		public String getObjectOfPrimitive() {
			return objectOfPrimitive;
		}

		public abstract String[] getTypeAndFormat();
	}
	
	// This method creates with the help of the enum the correct typeAndFormat array and returns it
	@Override
	public String[] getTypeAndFormat(String type) {
		// Iterate through enum values
		for (NumberEnum nh : NumberEnum.values()) {
			// check if the typeName equals the enum.getName(). If so then get array and return it
			if (type.equals(nh.getObjectOfPrimitive()) || type.equals(nh.getPrimitive())) {
				return nh.getTypeAndFormat();
			}
		}
		return null;
	}
}