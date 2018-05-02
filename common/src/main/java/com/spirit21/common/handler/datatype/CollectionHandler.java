package com.spirit21.common.handler.datatype;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spirit21.common.Consts;

/**
 * This handler handles the most common types of collections
 */
public class CollectionHandler implements TypeHandler {
	
	/**
	 * This enum saves the most common types of collections
	 */
	private enum CollectionEnum {
		COLLECTION(Collection.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.ARRAY;
				return typeAndFormat;
			}
		},
		LIST(List.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.ARRAY;
				return typeAndFormat;
			}
		},
		SET(Set.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.ARRAY;
				return typeAndFormat;
			}
		},
		MAP(Map.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				String[] typeAndFormat = new String[2];
				typeAndFormat[0] = Consts.MAP;
				return typeAndFormat;
			}
		};

		private final String name;

		private CollectionEnum(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public abstract String[] getTypeAndFormat();
	}
	
	/** 
	 * This method creates with the help of the enum the correct typeAndFormat array and returns it
	 */
	@Override
	public String[] getTypeAndFormat(String typeName) {
		for (CollectionEnum ae : CollectionEnum.values()) {
			if (typeName.equals(ae.getName())) {
				return ae.getTypeAndFormat();
			}
		}
		return null;
	}
}
