package com.spirit21.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Type;

public class ClassDocCache {

	private Map<String, ClassDoc> typeNameToClass = new HashMap<>();
	
	/** 
	 * Constructor which puts the qualifiedTypeName and the belonging ClassDoc into a map
	 */
	public ClassDocCache(List<ClassDoc> classDocs) {
		typeNameToClass = classDocs.stream()
							.collect(Collectors.toMap(Type::qualifiedTypeName, c -> c));
	}
	
	/** 
	 * This method finds a classDoc by Type
	 */
	public ClassDoc findByType(Type type) {
		String typeName = type.qualifiedTypeName();
		return typeNameToClass.get(typeName);
	}
	
	/**
	 * This method finds a classDoc by the simpleTypeName
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