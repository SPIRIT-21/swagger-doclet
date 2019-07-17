package com.spirit21.common.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.Type;

/**
 * A helper which provides functionality to cache all ClassDocs. 
 * 
 * @author mweidmann
 */
public class ClassDocCache {
	
	private Map<String, ClassDoc> classNameToClassDoc;
	
	public ClassDocCache() {
		this.classNameToClassDoc = new HashMap<>();
	}
	
	/**
	 * Initializes the ClassDocCache. It saves all ClassDocs in the above defined data structure.
	 * 
	 * @param classDocs All ClassDocs of the project saved in a List.
	 */
	public void init(List<ClassDoc> classDocs) {
		classDocs.forEach(classDoc -> classNameToClassDoc.put(classDoc.qualifiedTypeName(), classDoc));
	}
	
	/**
	 * Tries to find a cached ClassDoc by a passed type.
	 * 
	 * @param type The type for which the ClassDoc will be searched.
	 * @return The found ClassDoc or null if nothing was found.
	 */
	public ClassDoc findByType(Type type) {
		return classNameToClassDoc.get(type.qualifiedTypeName());
	}
	
	/**
	 * Tries to find a cached ClassDoc by a passed simple name of a ClassDoc.
	 * 
	 * @param classDocName The simple name of the ClassDoc which is searched.
	 * @return The found ClassDoc or null if nothing was found.
	 */
	public ClassDoc findBySimpleName(String classDocName) {
		return classNameToClassDoc.values().stream()
				.filter(value -> value.simpleTypeName().equals(classDocName))
				.findFirst()
				.orElse(null);
	}
}
