package com.spirit21;

import java.io.IOException;
import java.util.logging.Level;

import com.spirit21.exception.ApiParserException;
import com.spirit21.parser.Parser;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

import lombok.extern.java.Log;

@Log
public class Doclet {

	/**
	 * Starting point of the Doclet
	 * This method gets the output format and invokes the parser
	 * Standard output format is json
	 */
	public static boolean start(RootDoc rootDoc) {
		String outputType = getOption(rootDoc.options(), Consts.OUTPUT_TYPE);
		
		try {
			return new Parser(rootDoc, outputType).run();
		} catch (ApiParserException e) {
			log.log(Level.SEVERE, "Failed to parse your API entry point.", e);
			return false;
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to write or create the swagger file.", e);
			return false;
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
		}
		return 0;
	}

	/**
	 * Optional method which is also automatically invoked. It checks the existence
	 * of the outputType parameter.
	 */
	// TODO: commandline swagger version
	public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
		boolean outputType = false;

		for (String[] args : options) {
			if (args[0].equals(Consts.OUTPUT_TYPE)) {
				outputType = true;
			}
		}
		
		if (!outputType) {
			log.info("There is no '-type x' parameter in commandline used. Please specify an outputType. For now is the json-format used.");
		}
		
		return true;
	}

	/**
	 * Required method which allows the doclet to get the generics of a list/map..
	 */
	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}
}