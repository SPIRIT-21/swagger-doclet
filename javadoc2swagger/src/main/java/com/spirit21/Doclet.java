package com.spirit21;

import com.spirit21.common.Consts;
import com.spirit21.common.exception.SwaggerException;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.jaxrs.JaxRsDoclet;
import com.spirit21.spring.SpringBootDoclet;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

import lombok.extern.java.Log;

@Log
public class Doclet {

	
	/**
	 * Entry point of the Doclet
	 * This method gets the output format, swagger version and the backend-type
	 * Then it invokes the associated parser
	 * Standard output format is json
	 * Standard swagger version is 3
	 */
	public static boolean start(RootDoc rootDoc) throws SwaggerException {
		String outputType = getOption(rootDoc.options(), Consts.OUTPUT_TYPE);
		outputType = CommonHelper.checkOutputType(outputType);
		
		String version = getOption(rootDoc.options(), Consts.VERSION);
		version = CommonHelper.checkVersion(version);
		
		String backend = getOption(rootDoc.options(), Consts.BACKEND);
		
		// TODO
		if (backend.equals(Consts.SPRING)) {
			return SpringBootDoclet.start(rootDoc, outputType, version);
		} else if (backend.equals(Consts.JAXRS)){
			return JaxRsDoclet.start(rootDoc, outputType, version);
		} else {
			throw new SwaggerException("Invalid backend type. Please specify a correct backend type (jaxrs or spring).");
		}
	}
	
	/**
	 * This method gets the value of a command line argument
	 */
	private static String getOption(String[][] options, String option) {
		String tagName = "";
		
		for (String[] args : options) {
			if (args[0].equals(option)) {
				tagName = args[1];
				break;
			}
		}
		
		return tagName;
	}
	
	/**
	 * Required method to allow custom commandline parameter like '-type x'. This
	 * method determines the number of parts of the option. For example '-test that
	 * this' has 3 parts, while '-type x' has 2 parts. This method is automatically invoked.
	 */
	public static int optionLength(String option) {
		if (option.equals(Consts.OUTPUT_TYPE)) {
			return 2;
		} else if (option.equals(Consts.VERSION)) {
			return 2;
		} else if (option.equals(Consts.BACKEND)) {
			return 2;
		}
		return 0;
	}
	
	/**
	 * Optional method which is also automatically invoked. It checks the existence
	 * of the outputType parameter.
	 */
	public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
		boolean outputType = false;
		boolean version = false;
		boolean backend = false;

		for (String[] args : options) {
			if (args[0].equals(Consts.OUTPUT_TYPE)) {
				outputType = true;
			} else if (args[0].equals(Consts.VERSION)) {
				version = true;
			} else if (args[0].equals(Consts.BACKEND)) {
				backend = true;
			}
		}
		
		if (!outputType) {
			log.info("There is no '-type x' parameter in commandline used. "
					+ "Please specify an output format. For now is the json-format used.");
		}
		if (!version) {
			log.info("There is no '-version x' parameter in commandline used. "
					+ "Please specify a swagger version. For now is swagger version 3 used.");
		}
		if (!backend) {
			log.info("There is no '-backend x' parameter in commandline used. "
					+ "Please specify a backend type.");
		}
		return true;
	}
	
	/**
	 * Required method which allows the doclet to get the generics of a List/Map...
	 */
	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}
}
