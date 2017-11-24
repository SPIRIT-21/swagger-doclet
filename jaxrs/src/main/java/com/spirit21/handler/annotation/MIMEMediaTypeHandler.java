package com.spirit21.handler.annotation;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import com.spirit21.Consts;
import com.sun.javadoc.AnnotationDesc.ElementValuePair;

import io.swagger.models.Operation;

/** 
 * This enum svaes the produces and consumes annotation and adds the value of these annotations to the operation
 */
// TODO evp
public enum MIMEMediaTypeHandler {

	PRODUCES(Produces.class.getName()) {
		@Override
		public void setValue(Operation operation, ElementValuePair[] evp) {
			operation.addProduces(evp[0].value().toString().replaceAll(Consts.QUOTATION_MARK, Consts.EMPTY_STRING));
		}
	},
	CONSUMES(Consumes.class.getName()) {
		@Override
		public void setValue(Operation operation, ElementValuePair[] evp) {
			operation.addConsumes(evp[0].value().toString().replaceAll(Consts.QUOTATION_MARK, Consts.EMPTY_STRING));
		}
	};

	private final String operation;

	private MIMEMediaTypeHandler(String operation) {
		this.operation = operation;
	}

	public String getName() {
		return operation;
	}

	public abstract void setValue(Operation operation, ElementValuePair[] evp);
}