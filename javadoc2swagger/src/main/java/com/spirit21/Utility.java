package com.spirit21;

import com.spirit21.common.CommonConsts;

public class Utility {

	/**
	 * Checks if the CLI argument '-type' was correctly set by the user.
	 * Valid values are: 'json' or 'yaml'.
	 * 
	 * @param outputTypeArgument The from the user specified output type.
	 * @return If the user entered a valid argument then it will be returned. Otherwise the default value 'json'.
	 */
	public static String checkOutputType(String outputTypeArgument) {
		return outputTypeArgument.equals(Consts.OUTPUT_FORMAT_YAML) ? Consts.OUTPUT_FORMAT_YAML : CommonConsts.OUTPUT_FORMAT_JSON;
	}
	
	/**
	 * Checks if the CLI argument '-version' was correctly set by the user.
	 * Valid values are: '2' or '3'.
	 * 
	 * @param versionArgument The from the user specified version.
	 * @return If the user entered a valid argument then it will be returned. Otherwise the default value '3'.
	 */
	public static String checkVersion(String versionArgument) {
		return versionArgument.equals(Consts.SWAGGER_VERSION_2) ? Consts.SWAGGER_VERSION_2 : CommonConsts.SWAGGER_VERSION_3;
	}
}
