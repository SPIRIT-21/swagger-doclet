package com.spirit21.handler.annotation;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import com.spirit21.Consts;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.Operation;

/** 
 * This enum svaes the produces and consumes annotation and adds the value of these annotations to the operation
 */
public enum MIMEMediaTypeHandler {

	PRODUCES(Produces.class.getName()) {
		@Override
		public void setValue(Operation operation, MethodDoc methodDoc) {
			String annotationValue = ParserHelper.getAnnotationValue(methodDoc, getOperation(), Consts.VALUE);
			operation.addProduces(ParserHelper.replaceQuotationMarks(annotationValue));
		}
	},
	CONSUMES(Consumes.class.getName()) {
		@Override
		public void setValue(Operation operation, MethodDoc methodDoc) {
			String annotationValue = ParserHelper.getAnnotationValue(methodDoc, getOperation(), Consts.VALUE);
			operation.addConsumes(ParserHelper.replaceQuotationMarks(annotationValue));
		}
	};

	private final String operation;

	private MIMEMediaTypeHandler(String operation) {
		this.operation = operation;
	}

	public String getOperation() {
		return operation;
	}

	public abstract void setValue(Operation operation, MethodDoc methodDoc);
}