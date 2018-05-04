package com.spirit21.jaxrs;

import java.io.IOException;
import java.util.logging.Level;

import com.spirit21.common.exception.ApiParserException;
import com.spirit21.jaxrs.parser.Parser;
import com.sun.javadoc.RootDoc;

import lombok.extern.java.Log;

@Log
public class JaxRsDoclet {

	/**
	 * Starting point of the jax-rs doclet
	 */
	public static boolean start(RootDoc rootDoc, String outputType, String version) {
		try {
			return new Parser(rootDoc, outputType, version).run();
		} catch (ApiParserException e) {
			log.log(Level.SEVERE, "Failed to parse your API entry point.", e);
			return false;
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to write or create the swagger file.", e);
			return false;
		}
	}
}
