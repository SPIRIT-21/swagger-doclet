package com.spirit21;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.spirit21.common.Consts;
import com.spirit21.common.exception.ApiParserException;
import com.spirit21.common.exception.SwaggerException;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.common.parser.AbstractParser;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

import lombok.extern.java.Log;

/**
 * Entry class for the Doclet.
 * 
 * @author mweidmann
 */
@Log
public class Doclet {

	private static String[][] options;

	/**
	 * Entry method of the Doclet. Parses the output format, swagger version,
	 * filename and the backend-type out of the passed options. Then it invokes the
	 * associated parser.
	 * 
	 * Standard output format is json. Standard swagger version is 3.
	 *
	 * @param rootDoc Represents the root of the program structure information.
	 * @return A boolean whether the swagger file was successfully generated.
	 * @throws SwaggerException if something went wrong.
	 */
	public static boolean start(RootDoc rootDoc) throws SwaggerException {
		Map<String, String> arguments = new HashMap<>();
		options = rootDoc.options();

		arguments.put(Consts.CLI_OUTPUT_FORMAT, CommonHelper.checkOutputType(getOption(Consts.CLI_OUTPUT_FORMAT)));
		arguments.put(Consts.CLI_SWAGGER_VERSION, CommonHelper.checkVersion(getOption(Consts.CLI_SWAGGER_VERSION)));
		arguments.put(Consts.CLI_FILE_NAME, getOption(Consts.CLI_FILE_NAME));

		String backend = getOption(Consts.CLI_BACKEND_TYPE);
		AbstractParser parser = null;

		if (backend.equals(Consts.BACKEND_SPRING)) {
			parser = new com.spirit21.spring.parser.Parser(rootDoc, arguments);
		} else if (backend.equals(Consts.BACKEND_JAXRS)) {
			parser = new com.spirit21.jaxrs.parser.Parser(rootDoc, arguments);
		} else {
			throw new SwaggerException("Invalid backend type. Please specify a correct backend type (jaxrs or spring).");
		}

		return runParser(parser);
	}

	/**
	 * Runs the parser and throws an exception if something went wrong.
	 * 
	 * @param parser The parser which will be called.
	 * @return A boolean whether the swagger file was successfully generated.
	 */
	private static boolean runParser(AbstractParser parser) {
		try {
			return parser.run();
		} catch (ApiParserException e) {
			log.log(Level.SEVERE, "Failed to parse the API entry point.", e);
			return false;
		} catch (IOException e) {
			log.log(Level.SEVERE, "Failed to write or to create the swagger file.", e);
			return false;
		}
	}

	/**
	 * Gets the value for a option if it exists.
	 * 
	 * @param option The option for which the value is searched.
	 * @return The found value or an empty string if nothing was found.
	 */
	private static String getOption(String option) {
		return Arrays.asList(options).stream()
				.filter(args -> args[0].equals(option))
				.map(args -> args[1])
				.findFirst()
				.orElse("");
	}

	/**
	 * Required method to allow custom options like '-type x'. This method
	 * determines the number of parts of the option. For example '-test that this'
	 * has 3 parts, while '-type x' has 2 parts. This method is automatically
	 * invoked.
	 *
	 * @param option The allowed option.
	 * @return A number describing the number of parts of the option.
	 */
	public static int optionLength(String option) {
		if (option.equals(Consts.CLI_OUTPUT_FORMAT) 
				|| option.equals(Consts.CLI_SWAGGER_VERSION) 
				|| option.equals(Consts.CLI_BACKEND_TYPE)
				|| option.equals(Consts.CLI_FILE_NAME)) {
			return 2;
		}
		return 0;
	}

	/**
	 * Optional method which is also automatically invoked. It checks if the passed
	 * options are valid.
	 * 
	 * @param reporter Reporter for reporting erroneous options. Is needed for the Doclet invocation.
	 * @return A boolean whether the check was successful or not.
	 */
	public static boolean validOptions(DocErrorReporter reporter) {
		boolean outputType = false;
		boolean version = false;
		boolean backend = false;
		boolean filename = false;

		for (String[] args : options) {
			if (args[0].equals(Consts.CLI_OUTPUT_FORMAT)) {
				outputType = true;
			} else if (args[0].equals(Consts.CLI_SWAGGER_VERSION)) {
				version = true;
			} else if (args[0].equals(Consts.CLI_BACKEND_TYPE)) {
				backend = true;
			} else if (args[0].equals(Consts.CLI_FILE_NAME)) {
				filename = true;
			}
		}

		if (!outputType) {
			log.log(Level.INFO, "There is no '-type x' option used. Please specify an output format. For now is the json-format used.");
		}
		if (!version) {
			log.log(Level.INFO, "There is no '-version x' option used. Please specify a swagger version. For now is swagger version 3 used.");
		}
		if (!backend) {
			log.log(Level.INFO, "There is no '-backend x' option used. Please specify a backend type.");
		}
		if (!filename) {
			log.log(Level.INFO, "There is no '-filename x' option used. Please specify a file name.");
		}
		return true;
	}

	/**
	 * Required method which allows the Doclet to access the generics a Doc Element.
	 * 
	 * @return The language version for accessing generics.
	 */
	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}
}
