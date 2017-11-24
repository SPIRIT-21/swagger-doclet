package com.spirit21;

import java.io.IOException;
import java.util.logging.Level;

import com.spirit21.exception.ApiParserException;
import com.spirit21.parser.Parser;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

import lombok.extern.java.Log;

@Log
public class Doclet {
	
	/**
	 * Starting point of the Doclet
	 */
	public static boolean start(RootDoc rootDoc) {
		// Gets the swagger version
		String version = readOptions(rootDoc.options());
		try {
			return new Parser(rootDoc, version).run();
		//several exceptions that could occur
		} catch (ApiParserException e) {
			log.log(Level.SEVERE, "Failed to parse your API entry point.", e);
			return false;
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to write or create the .json swagger file.", e);
			return false;
		}
	}
	
	// TODO rewrite method
	/**
	 * This method gets the swagger version from command line
	 */
	private static String readOptions(String[][] options) {
		String tagName = null;
		for (int i = 0; i < options.length; i++) {
			String[] opt = options[i];
			if (opt[0].equals(Consts.VERSION)) {
				tagName = opt[1];
			}
		}
		return tagName;
	}
	
	/**
	 * Required method to allow custom commandline parameter like '-version x'
	 * This method determines the number of parts of the option
	 * For Example '-test that this' --> 3 '-version x' --> 2
	 * This method is automatically invoked.
	 */
	public static int optionLength(String option) {
		if (option.equals(Consts.VERSION)) {
			return 2;
		}
		return 0;
	}
	
	// FIXME Something went wrong when executing doclet on other projects
//	/*
//	 * Optional method which is also automatically invoked.
//	 * It checks the existence of the '-version' parameter
//	 */
//	public static boolean validOptions(String[][] options, DocErrorReporter reporter) {
//		boolean foundTagOption = false;
//		for (int i = 0; i < options.length; i++) {
//			String[] opt = options[i];
//			if (opt[0].equals(Consts.VERSION)) {
//				if (foundTagOption) {
//					log.info("Only one '-version' parameter in commandline is allowed. For now is swagger version 3 used.");
//				} else {
//					foundTagOption = true;
//				}
//			}
//		}
//		if (!foundTagOption) {
//			log.info("There is no '-version x' parameter in commandline. Please specify a version. For now is swagger version 3 used.");
//		}
//		return foundTagOption;
//	}
	
	/**
	 * Required method which allows the doclet to get the generics of a List/Map..
	 */
	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}
}