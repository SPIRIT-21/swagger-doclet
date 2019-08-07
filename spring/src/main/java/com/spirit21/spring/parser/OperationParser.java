package com.spirit21.spring.parser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PathVariable;

import com.spirit21.common.CommonConsts;
import com.spirit21.common.exception.OperationParserException;
import com.spirit21.common.handler.javadoc.ResponseTagHandler;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.spring.Consts;
import com.spirit21.spring.handler.parameter.ParameterFactory;
import com.spirit21.spring.helper.ParserHelper;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Tag;

import io.swagger.models.Operation;
import io.swagger.models.Response;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import lombok.extern.java.Log;

/**
 * Parses a MethodDoc to create operations for a mapping path.
 * Firstly the basic operation is created: the tags, media type, description etc. is set.
 * After that for every HTTP mapping a unique operation id is set and the resulting operation is
 * added to the data structure of the swagger path object. 
 * 
 * @author mweidmann
 */
@Log
public class OperationParser {

	private ParameterFactory parameterFactory;

	protected OperationParser() {
		this.parameterFactory = new ParameterFactory();
	}

	/**
	 * Starts the parsing process for swagger operations. First a map is created in which the HTTP method and 
	 * the operation is saved to. Then the operation is created and saved into the map.
	 * Afterwards the created map will be returned.
	 * 
	 * @param mappingPath The mapping path out of which the operation tags are created.
	 * @param methodDoc The MethodDoc out of which the operation is created.
	 * @return A map which contains all HTTP methods and the corresponding swagger operation for a mapping path.
	 */
	protected Map<String, Operation> run(String mappingPath, MethodDoc methodDoc) {
		Map<String, Operation> httpMethodToOperation = new HashMap<>();
		Operation swaggerOperation = createOperation(mappingPath, methodDoc);
		
		for (String httpMethod : ParserHelper.getSimpleHttpMethods(methodDoc)) {
			swaggerOperation.setOperationId(httpMethod + mappingPath);
			httpMethodToOperation.put(httpMethod, swaggerOperation);
		}
		return httpMethodToOperation;
	}

	/**
	 * Creates a swagger operation and calls other methods to set the properties of the created
	 * swagger operation.
	 * 
	 * @param mappingPath The mapping path out of which the operation tags are created.
	 * @param methodDoc The MethodDoc out of which the information for the swagger operation properties are taken.
	 * @return The fully configured swagger operation.
	 */
	private Operation createOperation(String mappingPath, MethodDoc methodDoc) {
		Operation swaggerOperation = new Operation();
		
		swaggerOperation.setTags(getTags(mappingPath));
		setMediaType(swaggerOperation, methodDoc);
		swaggerOperation.setDescription(getDescription(methodDoc));
		setParameters(methodDoc, swaggerOperation);
		swaggerOperation.setDeprecated(isDeprecated(methodDoc));
		
		try {
			setResponses(methodDoc, swaggerOperation);
		} catch (OperationParserException e) {
			log.log(Level.SEVERE, "Error while creating the responses for an operation for a path!", e);
		}
		
		return swaggerOperation;
	}
	
	/**
	 * Gets the tag of the swagger operation. Therefore the mapping path will be checked
	 * with a regular expression and the result is taken as the tag of the operation.
	 * 
	 * @param mappingPath The mapping path out of which the operation tags are created.
	 * @return A list containing all tags. (In this case, it is only one tag, due to requirements of this application)
	 */
	private List<String> getTags(String mappingPath) {
		Matcher matcher = Parser.TAG_NAME_PATTERN.matcher(mappingPath);
		return matcher.matches() ? Arrays.asList(matcher.group(1)) : Arrays.asList();
	}

	/**
	 * Sets the MIME media types of the operation.
	 * 
	 * @param operation The operation in which the MIME media types are set.
	 * @param methodDoc The MethodDoc to get media type values of the mapping annotation.
	 */
	private void setMediaType(Operation operation, MethodDoc methodDoc) {
		String mappingAnnotation = ParserHelper.getFullMappingAnnotation(methodDoc);

		// Adds produces to the operation.
		AnnotationValue annotationValue = CommonHelper.getAnnotationValue(methodDoc, mappingAnnotation, Consts.ANNOTATION_PROPERTY_PRODUCES);
		String[] value = (String[]) CommonHelper.getAnnotationValueObject(annotationValue);
		
		if (value != null) {
			Arrays.asList(value).forEach(operation::addProduces);
		}
		
		// Adds consumes to the operation.
		annotationValue = CommonHelper.getAnnotationValue(methodDoc, mappingAnnotation, Consts.ANNOTATION_PROPERTY_CONSUMES);
		value = (String[]) CommonHelper.getAnnotationValueObject(annotationValue);
		
		if (value != null) {
			Arrays.asList(value).forEach(operation::addConsumes);
		}
	}

	/**
	 * Gets the description of the passed MethodDoc by checking its JavaDoc comments.
	 * 
	 * @param methodDoc The MethodDoc from which the JavaDoc will be checked for a description.
	 * @return A string containing the description of the MethodDoc.
	 */
	private String getDescription(MethodDoc methodDoc) {
		return Arrays.asList(methodDoc.inlineTags()).stream()
				.map(Tag::text)
				.collect(Collectors.joining());
	}

	/**
	 * Creates and sets the responses for a swagger operation. If no response is documented an exception
	 * will be thrown.
	 * 
	 * @param methodDoc The MethodDoc in which JavaDoc documentation all responses are documented.
	 * @param operation The operation in which the created responses will be set.
	 * @throws OperationParserException If no response was documented.
	 */
	private void setResponses(MethodDoc methodDoc, Operation operation) throws OperationParserException {
		Map<String, Response> swaggerResponses = new HashMap<>();
		Response currentResponse = null;

		// Iterate through all JavaDoc tags.
		for (Tag javadocTag : methodDoc.tags()) {
			// If the tag does not begin with @responseCode, then the response properties can be set.
			if (!javadocTag.name().equals(CommonConsts.OPERATION_PARSER_RESPONSE_CODE)) {
				setResponseProperty(javadocTag, currentResponse);
				continue;
			}
			
			// If the tag does begin with @responseCode, then it is a new response object.
			currentResponse = new Response();
			swaggerResponses.put(javadocTag.text(), currentResponse);
		}

		if (swaggerResponses.isEmpty()) {
			throw new OperationParserException("In your documentation of the method '" + methodDoc.name() + "' in '"
					+ methodDoc.containingClass().qualifiedName() + "' you have to document at least one response!");
		}
		operation.setResponses(swaggerResponses);
	}

	/**
	 * Sets the swagger response object properties with the help of the ResponseTagHandler.
	 * Example for response object properties are the response schema or description.
	 * 
	 * @param javadocTag The tag which is potentially a response object property.
	 * @param swaggerResponse The swagger response in which the information of the tag will be set.
	 */
	private void setResponseProperty(Tag javadocTag, Response swaggerResponse) {
		Arrays.asList(ResponseTagHandler.values()).stream()
			.filter(responseTagHandler -> responseTagHandler.getTagName().equals(javadocTag.name()))
			.forEach(responseTagHandler -> responseTagHandler.setTagValueToResponseModel(javadocTag, swaggerResponse));
	}

	/**
	 * Creates all swagger parameters of a MethodDoc and adds them to the swagger operation.
	 * In addition to that it is checked whether the number of body parameters is correct.
	 * 
	 * @param methodDoc The MethodDoc which contains all parameters.
	 * @param swaggerOperation The swagger operation in which the created parameters will be set.
	 */
	private void setParameters(MethodDoc methodDoc, Operation swaggerOperation) {
		List<Parameter> swaggerParameters = Arrays.asList(methodDoc.parameters()).stream()
				.filter(parameter -> !CommonHelper.hasAnnotation(parameter, PathVariable.class.getName()))
				.map(parameter -> parameterFactory.createSwaggerParameter(methodDoc, parameter))
				.collect(Collectors.toList());

		checkBodyParameters(methodDoc, swaggerParameters);
		swaggerOperation.setParameters(swaggerParameters);
	}

	/**
	 * Checks the number of body parameters. If there are more than one, a warning is printed
	 * and all body parameter are removed except for the first. The reason for that is to get 
	 * a valid swagger file.
	 * 
	 * @param methodDoc The MethodDoc to get correct logging if it is needed.
	 * @param swaggerParameters All swagger parameters of the MethodDoc.
	 */
	private void checkBodyParameters(MethodDoc methodDoc, List<Parameter> swaggerParameters) {
		List<Parameter> swaggerBodyParameters = swaggerParameters.stream()
				.filter(swaggerParameter -> swaggerParameter instanceof BodyParameter)
				.collect(Collectors.toList());
		
		if (swaggerBodyParameters.size() > 1) {
			swaggerBodyParameters.remove(0);
			swaggerParameters.removeAll(swaggerBodyParameters);
			log.warning("Only one body parameter is here allowed: " 
						+ methodDoc.containingClass().qualifiedName() + " " + methodDoc.name());
		}
	}
	
	/**
	 * Checks if the passed MethodDoc or the containing ClassDoc is deprecated.
	 * If yes, true is returned, otherwise false.
	 * 
	 * @param methodDoc The MethodDoc which should be checked.
	 * @return True if it is deprecated, otherwise false.
	 */
	private boolean isDeprecated(MethodDoc methodDoc) {
		return CommonHelper.hasAnnotation(methodDoc.containingClass(), Deprecated.class.getName()) 
				|| CommonHelper.hasAnnotation(methodDoc, Deprecated.class.getName());
	}
}
