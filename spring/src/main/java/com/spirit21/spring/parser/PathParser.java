package com.spirit21.spring.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PathVariable;

import com.spirit21.common.CommonConsts;
import com.spirit21.common.helper.CommonHelper;
import com.spirit21.spring.Consts;
import com.spirit21.spring.handler.annotation.HttpMethodHandler;
import com.spirit21.spring.handler.parameter.PathParameterHandler;
import com.spirit21.spring.helper.ParserHelper;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.Parameter;

import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.PathParameter;

/**
 * Parses the controller ClassDocs and its MethodDocs to create swagger paths.
 * Firstly, the ClassDocs are sorted to a data structure (Map<String, List<MethodDoc>>).
 * There is the mapping path saved to a list of MethodDocs which handle the requests for this path.
 * After that for every mapping path a swagger path is generated and it will be looked for path parameters.
 * Lastly, the swagger operation is generated and set into the path.
 * 
 * @author mweidmann
 */
public class PathParser {
	
	// This pattern is used to find out the names of the path parameters out of a mapping path.
	// E.g. /test/{uid}/foo/{id} finds "uid" and "id".
	private final Pattern pathParameterNamePattern = Pattern.compile("(?<=\\{)([a-zA-Z0-9_-]+)(?=\\})");
	
	// For every mapping (e.g. /test) the swagger Path object is saved in this map.
	private Map<String, Path> swaggerPaths;
	// For every mapping (e.g. /hello) all related MethodDocs are saved in this map.
	private Map<String, List<MethodDoc>> mappingValueToMethodDocs;

	private PathParameterHandler pathParameterHandler;
	private OperationParser operationParser;
	
	protected PathParser() {
		this.swaggerPaths = new LinkedHashMap<>();
		this.mappingValueToMethodDocs = new LinkedHashMap<>();
		
		this.pathParameterHandler = new PathParameterHandler(PathVariable.class.getName());
		this.operationParser = new OperationParser();
	}

	/**
	 * Starts the parsing process for swagger paths. First the ClassDocs/MethodDocs are sorted 
	 * then the real parsing process begins. Afterwards the paths will be set to the passed
	 * Swagger object.
	 * 
	 * @param swagger The Swagger object into which the paths will be set.
	 */
	protected void run(Swagger swagger) {
		// Sort ClassDocs/MethodDocs.
		Parser.CONTROLLER_CLASS_DOCS.forEach(this::sortMethodDocsByPathMapping);
		// Create a path for every entry in the map.
		mappingValueToMethodDocs.entrySet().forEach(this::createSwaggerPath);
		swagger.setPaths(swaggerPaths);
	}
	
	/**
	 * Creates the swagger path object and checks if path parameters exist.
	 * If so, then the path parameters are created and added to the swagger path object.
	 * After that, the swagger operations for the path are created. Lastly, the created path
	 * is added to the map.
	 * 
	 * @param entry The map entry which contains the path and the list of MethodDocs.
	 */
	private void createSwaggerPath(Entry<String, List<MethodDoc>> entry) {
		Path swaggerPath = new Path();
		
		createAllSwaggerPathParameters(entry.getKey(), entry.getValue()).forEach(swaggerPath::addParameter);
		createAllOperationsForPath(swaggerPath, entry.getKey(), entry.getValue());
		
		swaggerPaths.put(entry.getKey(), swaggerPath);
	}
	
	/**
	 * Sorts all HTTP-MethodDocs by PathMapping and puts them in a map. Firstly, all controller 
	 * mappings are parsed. During the iteration through all controller mappings, the MethodDocs
	 * are parsed for their mappings. Then the controller mappings and the method mappings are
	 * concatenated and put into the map. 
	 * 
	 * @param classDoc The ClassDoc out of which the mapping and the MethodDocs are coming from.
	 */
	private void sortMethodDocsByPathMapping(ClassDoc classDoc) {
		List<String> controllerMappings = ParserHelper.getAllControllerMappings(classDoc);
		
		// If the list is empty, there should be at least one path.
		if (controllerMappings.isEmpty()) {
			controllerMappings.add("/");
		}
		
		for (String controllerMapping : controllerMappings) {
			for (MethodDoc methodDoc : classDoc.methods()) {
				// Get the mappings for the MethodDoc and is an implicit check for a mapping annotation.
				AnnotationValue[] methodMappings = ParserHelper.getMappingPath(methodDoc);
				
				// If the MethodDoc does not contain any mapping, it can be added to the map directly.
				if (methodMappings.length == 0) {
					putPathMappingAndMethodDoc(controllerMapping, methodDoc);
					continue;
				}
				
				List<String> fullMappings = ParserHelper.getAllFullMappingsOfMethod(controllerMapping, methodMappings);
				fullMappings.forEach(fullMapping -> putPathMappingAndMethodDoc(fullMapping, methodDoc));
			}
		}
	}
	
	/**
	 * Adds the mapping value and the passed MethodDoc into the mappingValueToMethodDocs map.
	 * First it is checked whether the path mapping value exists. If so, then the MethodDoc is added to
	 * the list. If not then a new entry is created.
	 * 
	 * @param fullMapping The full mapping value (ClassDoc-Mapping + MethodDoc-Mapping).
	 * @param methodDoc The MethodDoc for the the mapping value.
	 */
	private void putPathMappingAndMethodDoc(String fullMapping, MethodDoc methodDoc) {
		if (mappingValueToMethodDocs.containsKey(fullMapping)) {
			mappingValueToMethodDocs.get(fullMapping).add(methodDoc);
			return;
		}
		
		List<MethodDoc> methodDocs = new ArrayList<>();
		methodDocs.add(methodDoc);
		mappingValueToMethodDocs.put(fullMapping, methodDocs);
	}
	
	/**
	 * Creates a set of swagger path parameters and returns it. Firstly, the names of the
	 * path parameter are parsed out of the mapping path. After that the swagger path parameters
	 * are created and put into a set. A set is used to filter duplicated elements. After that the 
	 * created set is returned.
	 * 
	 * @param mappingPath The mapping path out of which the path parameters are taken.
	 * @param methodDocs A list of MethodDocs mapped to via the mapping path. Used to get information about the path parameters.
	 * @return A set of all swagger path parameters of the passed path. 
	 */
	private Set<PathParameter> createAllSwaggerPathParameters(String mappingPath, List<MethodDoc> methodDocs) {
		Set<String> pathParameterNames = ParserHelper.getPathParameterNames(pathParameterNamePattern, mappingPath);
		
		return methodDocs.stream()
				.filter(ParserHelper::hasMappingAnnotation)
				.map(methodDoc -> checkAndCreateParameter(methodDoc, pathParameterNames))
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
	}
	
	/**
	 * Checks the parameters of a method for a path parameter annotation and if they are used in the
	 * mapping path. If these checks were passed successfully swagger path parameters are created for 
	 * every parameter and added to a set.
	 * 
	 * @param methodDoc The MethodDoc from which the parameters are taken for the check and creation of the swagger path parameters.
	 * @param pathParamNames The set which contains all path parameter names of a mapping path.
	 * @return A set containing all created swagger path parameters.
	 */
	private Set<PathParameter> checkAndCreateParameter(MethodDoc methodDoc, Set<String> pathParamNames) {
		return Arrays.asList(methodDoc.parameters()).stream()
				.filter(parameter -> CommonHelper.hasAnnotation(parameter, pathParameterHandler.getHttpParameterType()))
				.filter(parameter -> isCorrectPathParameter(parameter, pathParamNames))
				.map(parameter -> pathParameterHandler.createNewSwaggerParameter(methodDoc, parameter))
				.collect(Collectors.toSet());
	}
	
	/**
	 * Checks if a passed JavaDoc parameter is a parameter of a specific mapping path.
	 * With the set of path parameter names of the mapping path these things are checked:
	 * 	1) Does the set contains the parameter name?
	 * 	2) Does the set contains the path parameter annotation property value "name" or "value"?
	 * If one of these two things is true, the path parameter name is removed from the set and true is returned.
	 * If not, then false is returned.
	 * 
	 * @param parameter The JavaDoc parameter which should be checked.
	 * @param pathParameterNames The set which contains all path parameter names of a mapping path.
	 * @return True if the parameter is valid (was in the set), otherwise false.
	 */
	private boolean isCorrectPathParameter(Parameter parameter, Set<String> pathParameterNames) {
		if (pathParameterNames.contains(parameter.name())) {
			pathParameterNames.remove(parameter.name());
			return true;
		}
		
		String value = (String) ParserHelper.getAnnotationValueOfTwoProperties(parameter, pathParameterHandler.getHttpParameterType(), Consts.ANNOTATION_PROPERTY_NAME, CommonConsts.ANNOTATION_PROPERTY_NAME_VALUE);
		
		if (pathParameterNames.contains(value)) {
			pathParameterNames.remove(value);
			return true;
		}
		return false;
	}
	
	/**
	 * Creates for every MethodDoc of a mapping path a swagger operation with the help of the operation parser.
	 * After that the created operations will be set to the swagger path.
	 * 
	 * @param swaggerPath The swagger path in which all generated operations will be set.
	 * @param mappingPath The mapping path which helps the operation parser to create an operation. 
	 * @param methodDocs A list of MethodDocs mapped to via the mapping path. For every MethodDoc some operations will be created.
	 */
	private void createAllOperationsForPath(Path swaggerPath, String mappingPath, List<MethodDoc> methodDocs) {
		methodDocs.stream()
			.map(methodDoc -> operationParser.run(mappingPath, methodDoc))
			.map(Map::entrySet)
			.flatMap(Set::stream)
			.forEach(operationEntry -> setOperationToPath(swaggerPath, operationEntry.getKey(), operationEntry.getValue()));
	}
	
	/**
	 * Sets the generated operation to the swagger path. Needed is the information what for an HTTP method it is.
	 * 
	 * @param swaggerPath The swagger path in which the generated operation will be set.
	 * @param httpMethod The HTTP method as which the operation is stored in the path. 
	 * @param swaggerOperation The generated swagger operation which will be set into the swagger path.
	 */
	private void setOperationToPath(Path swaggerPath, String httpMethod, Operation swaggerOperation) {
		Arrays.asList(HttpMethodHandler.values()).stream()
			.filter(httpMethodHandler -> httpMethodHandler.getSimpleHttpMethodName().equals(httpMethod))
			.forEach(httpMethodHandler -> httpMethodHandler.setOperationToPath(swaggerOperation, swaggerPath));
	}
}
