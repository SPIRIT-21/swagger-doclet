package com.spirit21.jaxrs.helper;

import java.util.Arrays;
import java.util.Objects;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;

import com.spirit21.common.Consts;
import com.spirit21.common.helper.AnnotationHelper;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.jaxrs.handler.annotation.HttpMethodHandler;
import com.spirit21.jaxrs.parser.Parser;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;

public class ParserHelper {

	/**
	 * This method checks if a resource has any methods with HTTP-annotations
	 */
	public static boolean isResource(ClassDoc classDoc) {
		for (MethodDoc methodDoc : classDoc.methods()) {
			if (isHttpMethod(methodDoc)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * This method gets the simple http method of a programElementDoc
	 */
	public static String getSimpleHttpMethod(ProgramElementDoc programElementDoc) {
		HttpMethodHandler httpMethodHandler = getHttpMethodHandler(programElementDoc);
		
		if (httpMethodHandler != null) {
			return httpMethodHandler.getSimpleName();
		}
		return null;
	}
	
	/**
	 * This method gets the full http method of a programElementDoc
	 */
	public static String getFullHttpMethod(ProgramElementDoc programElementDoc) {
		HttpMethodHandler httpMethodHandler = getHttpMethodHandler(programElementDoc);
		
		if (httpMethodHandler != null) {
			return httpMethodHandler.getFullName();
		}
		return null;
	}
	
	/**
	 * This method gets the right handler for the programElementDoc
	 */
	private static HttpMethodHandler getHttpMethodHandler(ProgramElementDoc programElementDoc) {
		AnnotationHelper annotationHelper = new AnnotationHelper(programElementDoc.annotations());
		
		for (HttpMethodHandler httpMethodHandler : HttpMethodHandler.values()) {
			if (annotationHelper.isAnnotatedBy(httpMethodHandler.getFullName())) {
				return httpMethodHandler;
			}
		}
		return null;
	}
	
	/**
	 * This method checks if a programElementDoc has a HTTP-method-annotation (javax.ws.rs.GET/POST/PUT/DELETE)
	 */
	public static boolean isHttpMethod(ProgramElementDoc programElementDoc) {
		return getFullHttpMethod(programElementDoc) != null;
	}
	
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
				.filter(m -> !isHttpMethod(m) && m.returnType().equals(classDoc))
				.map(m -> CommonHelper.getAnnotationValue(m, Path.class.getName(), Consts.VALUE))
				.filter(Objects::nonNull)
				.map(CommonHelper::getAnnotationValueObject)
				.filter(Objects::nonNull)
				.forEach(aValue -> {
					sb.append("/" + aValue);
					sb.insert(0, getPath(parent));
				});
			
			return CommonHelper.replaceSlashes(sb.toString());
		} else {
			AnnotationValue aValue = CommonHelper.getAnnotationValue(classDoc, Path.class.getName(), Consts.VALUE);
			String value = "/" + (String) CommonHelper.getAnnotationValueObject(aValue);
			return CommonHelper.replaceSlashes(value);
		}
	}
}