package com.spirit21.common.handler.datatype;

import com.spirit21.common.Consts;
import com.spirit21.common.handler.datatype.ObjectTypeHandler.ObjectType;;

/**
 * This handler handles Objects.
 * 
 * @author mweidmann
 */
public class ObjectTypeHandler extends AbstractTypeHandler<ObjectType> {

	public ObjectTypeHandler() {
		super(ObjectType.class);
	}

	/**
	 * Enum which contains the Object type.
	 * 
	 * @author mweidmann
	 */
	public enum ObjectType implements ITypeEnum {
		OBJECT(Object.class.getName()) {
			@Override
			public String[] getTypeAndFormat() {
				return new String[] { Consts.OBJECT, null };
			}
		};

		private final String typeName;

		private ObjectType(String typeName) {
			this.typeName = typeName;
		}

		@Override
		public String getTypeName() {
			return typeName;
		}
	}
}
