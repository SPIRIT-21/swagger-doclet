package com.spirit21.helper;

import java.util.Arrays;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;

import com.spirit21.Consts;
import com.spirit21.handler.annotation.HttpMethodHandler;
import com.spirit21.handler.datatype.DataTypeFactory;
import com.spirit21.parser.Parser;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Type;

import io.swagger.models.properties.ArrayProperty;
import io.swagger.models.properties.MapProperty;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.PropertyBuilder;
import io.swagger.models.properties.RefProperty;

public class ParserHelper {

	/**
	 * This method checks if a programElementDoc (classDoc, methodDoc) has a specific annotation
	 */
	public static boolean hasAnnotation(ProgramElementDoc programElementDoc, String annotation) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		return annotationHelper.isAnnotatedBy(annotation);
	}

	/**
	 * This method checks if a parameter has a specific annotation
	 */
	public static boolean hasAnnotation(Parameter parameter, String annotation) {
		AnnotationHelper annotationHelper = new AnnotationHelper(parameter.annotations());
		return annotationHelper.isAnnotatedBy(annotation);
	}
	
	/**
	 * This method gets the annotationValue of a specific annotation of a programElementDoc
	 */
	public static String getAnnotationValue(ProgramElementDoc programElementDoc, String annotationType, String specificAnnotation) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		return annotationHelper.getAnnotationValue(annotationType, specificAnnotation);
	}
	
	/**
	 * This method gets the annotationValue of a specific annotation of a parameter
	 */
	public static String getAnnotationValue(Parameter parameter, String annotationType, String specificAnnotation) {
		AnnotationHelper annotationHelper = new AnnotationHelper(parameter.annotations());
		return annotationHelper.getAnnotationValue(annotationType, specificAnnotation);
	}
	
	/**
	 * This method checks if a resource has any methods with HTTP-annotations
	 */
	public static boolean isResource(ClassDoc classDoc) {
		for (MethodDoc methodDoc : classDoc.methods()) {
			if (hasHttpMethod(methodDoc)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method returns the parentResourceClassDoc of any (sub..)ResourceClassDoc
	 */
	public static ClassDoc getParentClassDoc(ClassDoc classDoc) {
		if (Parser.resourceClassDocs.containsKey(classDoc)) {
			if (Parser.resourceClassDocs.get(classDoc) == null) {
				return classDoc;
			} else {
				return getParentClassDoc(Parser.resourceClassDocs.get(classDoc));
			}
		}
		return null;
	}

	/**
	 * This method returns the complete path of any classDoc
	 */
	public static String getPath(ClassDoc classDoc) {
		ClassDoc parent = Parser.resourceClassDocs.get(classDoc);
		// check if classDoc has a parent
		if (parent != null) {
			StringBuilder sb = new StringBuilder();
			
			// iterate through parent methods and get the method which returns the subResource
			Arrays.asList(parent.methods()).stream()
				.filter(ParserHelper::hasPathAnnotation)
				.filter(m -> !hasHttpMethod(m) && m.returnType().equals(classDoc))
				.map(m -> getAnnotationValue(m, Path.class.getName(), Consts.VALUE))
				.forEach(v -> {
					sb.append("/" + v);
					sb.insert(0, getPath(parent));
				});
			return replaceQuotationMarkAndSlashes(sb.toString());
		} else {
			String annotationValue = "/" + getAnnotationValue(classDoc, Path.class.getName(), Consts.VALUE);
			return replaceQuotationMarkAndSlashes(annotationValue);			
		}
	}
	
	/**
	 * This method helps to replace quotation marks and slashes
	 */
	private static String replaceQuotationMarkAndSlashes(String replace) {
		return replace.replaceAll(Consts.QUOTATION_MARK, Consts.EMPTY_STRING).replaceAll(Consts.SLASHES, Consts.SLASHES_REPLACE);
	}
	
	/**
	 * This method helps to replace quotation marks
	 */ 
	public static String replaceQuotationMarks(String replace) {
		return replace.replaceAll(Consts.QUOTATION_MARK, Consts.EMPTY_STRING);
	}

	/**
	 * This method returns the type and format of a property in this format
	 * String[0] = type, String[1] = format
	 */
	public static String[] checkTypeAndFormat(Type type) {
		String[] typeAndFormat = null;
		// If the type is a classDoc --> type = ref
		if (Parser.classDocCache.findByType(type) != null) {
			typeAndFormat = new String[2];
			typeAndFormat[0] = Consts.REF;
			return typeAndFormat;
		} else {
			DataTypeFactory dataTypeFactory = new DataTypeFactory();
			typeAndFormat = dataTypeFactory.getDataType(type.qualifiedTypeName());
			return typeAndFormat;
		}
	}

	/**
	 * This method creates properties with the help of typeAndFormat NOTE: This
	 * method creates some properties without the PropertyBuilder because of
	 * problems of the builder
	 */
	// TODO Maybe PropertyFactory?
	public static Property createProperty(String[] typeAndFormat, Type type) {
		if (typeAndFormat != null) {
			// If type = ref then create RefProperty
			if (typeAndFormat[0].equals(Consts.REF)) {
				RefProperty property = new RefProperty(type.simpleTypeName());
				addToEntityList(Parser.classDocCache.findByType(type));
				return property;
				// else if type = array then create ArrayProperty and set items (recursion)
			} else if (typeAndFormat[0].equals(Consts.ARRAY)) {
				ArrayProperty property = new ArrayProperty();
				for (Type newType : type.asParameterizedType().typeArguments()) {
					property.setItems(createProperty(checkTypeAndFormat(newType), newType));
				}
				return property;
				// else if type = map then create MapProperty and set additionalProperties
			} else if (typeAndFormat[0].equals(Consts.MAP)) {
				MapProperty property = new MapProperty();
				String[] temp = checkTypeAndFormat(type.asParameterizedType().typeArguments()[1]);
				property.additionalProperties(createProperty(temp, type.asParameterizedType().typeArguments()[1]));
				return property;
				// else create property with PropertyBuilder
			} else {
				return PropertyBuilder.build(typeAndFormat[0], typeAndFormat[1], null);
			}
		// If the property is not known, create RefProperty
		} else {
			String[] temp = new String[2];
			temp[0] = Consts.REF;
			addToEntityList(type.asClassDoc());
			return createProperty(temp, type);
		}
	}

	/**
	 * This method adds a classDoc to the definitionClassDoc list in the Parser
	 */
	public static void addToEntityList(ClassDoc classDoc) {
		if (!Parser.definitionClassDocs.contains(classDoc) && classDoc != null) {
			Parser.definitionClassDocs.add(classDoc);
		}
	}
	
	// FROM HERE NO COMMON METHODS. NEEDED METHODS ARE MARKED WITH A *
	/**
	 * This method checks if a programElementDoc (classDoc, methodDoc) has a javax.ws.rs.ApplicationPath annotation
	 */
	public static boolean hasApplicationPathAnnotation(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		return annotationHelper.isAnnotatedBy(ApplicationPath.class.getName());
	}
	
	/**
	 * This method checks if a programElementDoc has the javax.ws.rs.Path annotation
	 */
	public static boolean hasPathAnnotation(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		return annotationHelper.isAnnotatedBy(Path.class.getName());
	}

	/**
	 * This method checks if a programElementDoc has a HTTP-method-annotation (javax.ws.rs.GET/POST/PUT/DELETE)
	 */
	public static boolean hasHttpMethod(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		for (HttpMethodHandler hmh : HttpMethodHandler.values()) {
			if (annotationHelper.isAnnotatedBy(hmh.getFullName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method gets the full HTTP-method of a programElementDoc (javax.ws.rs.GET/POST..)
	 */
	public static String getFullHttpMethod(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		for (HttpMethodHandler httpMethod : HttpMethodHandler.values()) {
			if (annotationHelper.isAnnotatedBy(httpMethod.getFullName())) {
				return httpMethod.getFullName();
			}
		}
		return null;
	}

	/**
	 * This method gets the simple HTTP-method of a programElementDoc (GET/POST/PUT/DELETE)
	 */
	public static String getSimpleHttpMethod(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		for (HttpMethodHandler httpMethod : HttpMethodHandler.values()) {
			if (annotationHelper.isAnnotatedBy(httpMethod.getFullName())) {
				return httpMethod.getSimpleName();
			}
		}
		return null;
	}
}