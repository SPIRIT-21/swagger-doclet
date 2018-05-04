package com.spirit21.jaxrs.handler.annotation;

import java.util.Arrays;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import com.spirit21.common.Consts;
import com.spirit21.common.helper.CommonHelper;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.MethodDoc;

import v2.io.swagger.models.Operation;

/** 
 * This enum saves the produces and consumes annotation and adds the value of these annotations to the operation
 */
public enum MIMEMediaTypeHandler {

	PRODUCES(Produces.class.getName()) {
		@Override
		public void setValue(Operation operation, MethodDoc methodDoc) {
			AnnotationValue[] aValues = getAnnotationValues(methodDoc);
			
			Arrays.asList(aValues).stream()
				.map(annValue -> (String) annValue.value())
				.forEach(operation::addProduces);
		}
	},
	CONSUMES(Consumes.class.getName()) {
		@Override
		public void setValue(Operation operation, MethodDoc methodDoc) {
			AnnotationValue[] aValues = getAnnotationValues(methodDoc);
			
			Arrays.asList(aValues).stream()
				.map(annValue -> (String) annValue.value())
				.forEach(operation::addConsumes);
		}
	};

	private final String mimeMediaType;

	private MIMEMediaTypeHandler(String operation) {
		this.mimeMediaType = operation;
	}

	public String getMimeMediaType() {
		return mimeMediaType;
	}
	
	protected AnnotationValue[] getAnnotationValues(MethodDoc methodDoc) {
		AnnotationValue aValue = CommonHelper.getAnnotationValue(methodDoc, getMimeMediaType(), Consts.VALUE);
		return (AnnotationValue[]) CommonHelper.getAnnotationValueObject(aValue);
	}

	public abstract void setValue(Operation operation, MethodDoc methodDoc);
}