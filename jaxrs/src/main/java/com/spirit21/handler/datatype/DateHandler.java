package com.spirit21.handler.datatype;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Date;

import com.spirit21.Consts;

/** 
 * This handler handles the most common types of Date
 */
public class DateHandler implements TypeHandler {
	
	/**
	 * This enum saves the most common Date, LocalDateTime..
	 */
	private enum DateEnum {
		DATE(Date.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.STRING;
				typeAndFormat[1] = Consts.DATE;
				return typeAndFormat;
			}
		},
		LOCAL_DATE_TIME(LocalDateTime.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.STRING;
				typeAndFormat[1] = Consts.DATE_TIME;
				return typeAndFormat;
			}
		},
		LOCAL_DATE(LocalDate.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.STRING;
				typeAndFormat[1] = Consts.DATE;
				return typeAndFormat;
			}
		},
		OFFSET_DATE_TIME(OffsetDateTime.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.STRING;
				typeAndFormat[1] = Consts.DATE;
				return typeAndFormat;
			}
		},
		LOCAL_TIME(LocalTime.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.STRING;
				typeAndFormat[1] = Consts.DATE;
				return typeAndFormat;
			}
		},
		OFFSET_TIME(OffsetTime.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.STRING;
				typeAndFormat[1] = Consts.DATE;
				return typeAndFormat;
			}
		};

		private final String name;

		private DateEnum(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public abstract String[] getTypeAndFormat();
	}
	
	/**
	 * This method creates with the help of the enum the correct typeAndFormat Array and returns it
	 */
	@Override
	public String[] getTypeAndFormat(String typeName) {
		for (DateEnum dh : DateEnum.values()) {
			if (dh.getName().equals(typeName)) {
				return dh.getTypeAndFormat();
			}
		}
		return null;
	}
}