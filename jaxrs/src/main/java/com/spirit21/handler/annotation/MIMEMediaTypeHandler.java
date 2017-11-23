package com.spirit21.handler.annotation;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import com.spirit21.Consts;
import com.spirit21.helper.ParserHelper;
import com.sun.javadoc.MethodDoc;

import io.swagger.models.Operation;

// This enum svaes the produces and consumes annotation and adds the value of these annotations to the operation
public enum MIMEMediaTypeHandler {

	PRODUCES(Produces.class.getName()) {
		@Override
		public void setValue(Operation operation, MethodDoc methodDoc) {
			String s = ParserHelper.getAnnotationValue(methodDoc, getName(), Consts.VALUE);
			operation.addProduces(ParserHelper.replaceQuotationMarks(s));
		}
	},
	CONSUMES(Consumes.class.getName()) {
		@Override
		public void setValue(Operation operation, MethodDoc methodDoc) {
			String s = ParserHelper.getAnnotationValue(methodDoc, getName(), Consts.VALUE);
			operation.addConsumes(ParserHelper.replaceQuotationMarks(s));
		}
	};

	private final String operation;

	private MIMEMediaTypeHandler(String operation) {
		this.operation = operation;
	}

	public String getName() {
		return operation;
	}

	public abstract void setValue(Operation operation, MethodDoc methodDoc);
}