package com.spirit21.common.handler.datatype;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.spirit21.common.CommonConsts;
import com.spirit21.common.handler.datatype.CollectionTypeHandler.CollectionTypes;

/**
 * This handler handles the most common Collection types.
 * 
 * @author mweidmann
 */
public class CollectionTypeHandler extends AbstractTypeHandler<CollectionTypes> {

	public CollectionTypeHandler() {
		super(CollectionTypes.class);
	}

	/**
	 * Enum which contains the most common Collection types.
	 * 
	 * @author mweidmann
	 */
	public enum CollectionTypes implements ITypeEnum {
		COLLECTION(Collection.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { CommonConsts.PROPERTY_TYPE_ARRAY, null };
			}
		},
		LIST(List.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { CommonConsts.PROPERTY_TYPE_ARRAY, null };
			}
		},
		SET(Set.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { CommonConsts.PROPERTY_TYPE_ARRAY, null };
			}
		},
		MAP(Map.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { CommonConsts.PROPERTY_TYPE_MAP, null };
			}
		};

		private final String typeName;

		private CollectionTypes(String typeName) {
			this.typeName = typeName;
		}

		@Override
		public String getTypeName() {
			return typeName;
		}
	}
}
