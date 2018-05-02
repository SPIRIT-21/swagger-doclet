package com.spirit21.common.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Type;

public class ClassDocCache {
	
	private Map<String, ClassDoc> typeNameToClass = new HashMap<>();
	
	public ClassDocCache(List<ClassDoc> classDocs) {
		classDocs.forEach(classDoc -> typeNameToClass.put(classDoc.qualifiedTypeName(), classDoc));
	}
	
	/**
	 * This method finds a classDoc by type
	 */
	public ClassDoc findByType(Type type) {
		String typeName = type.qualifiedTypeName();
		return typeNameToClass.get(typeName);
	}
	
	/**
	 * This method finds a classDoc by the simple class name
	 */
	public ClassDoc findBySimpleName(String simpleName) {
		for (Entry<String, ClassDoc> entry : typeNameToClass.entrySet()) {
			if (entry.getValue().simpleTypeName().equals(simpleName)) {
				return entry.getValue();
			}
		}
		return null;
	}
}
