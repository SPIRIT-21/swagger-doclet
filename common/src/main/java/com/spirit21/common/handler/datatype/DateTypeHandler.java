package com.spirit21.common.handler.datatype;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.Date;

import com.spirit21.common.Consts;
import com.spirit21.common.handler.datatype.DateTypeHandler.DateTypes;

/**
 * This handler handles the most common Date types.
 * 
 * @author mweidmann
 */
public class DateTypeHandler extends AbstractTypeHandler<DateTypes> {

	public DateTypeHandler() {
		super(DateTypes.class);
	}

	/**
	 * Enum which contains the most common Date types.
	 * 
	 * @author mweidmann
	 */
	public enum DateTypes implements ITypeEnum {
		DATE(Date.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.STRING, Consts.DATE };
			}
		},
		LOCAL_DATE_TIME(LocalDateTime.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.STRING, Consts.DATE_TIME };
			}
		},
		LOCAL_DATE(LocalDate.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.STRING, Consts.DATE };
			}
		},
		OFFSET_DATE_TIME(OffsetDateTime.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.STRING, Consts.DATE };
			}
		},
		LOCAL_TIME(LocalTime.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.STRING, Consts.DATE };
			}
		},
		OFFSET_TIME(OffsetTime.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.STRING, Consts.DATE };
			}
		},
		INSTANT(Instant.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.STRING, Consts.DATE };
			}
		};

		private final String typeName;

		private DateTypes(String typeName) {
			this.typeName = typeName;
		}

		@Override
		public String getTypeName() {
			return typeName;
		}
	}
}
