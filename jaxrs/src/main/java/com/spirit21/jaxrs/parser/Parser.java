package com.spirit21.jaxrs.parser;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.spirit21.common.exception.ApiParserException;
import com.spirit21.common.parser.AbstractParser;
import com.spirit21.jaxrs.helper.ParserHelper;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.RootDoc;

public class Parser extends AbstractParser {

	public static Map<ClassDoc, ClassDoc> resourceClassDocs;

	private TagParser tagParser;
	private PathParser pathParser;

	/**
	 * Initialize
	 */
	public Parser(RootDoc rootDoc, Map<String, String> arguments) {
		super(rootDoc, arguments);

		this.tagParser = new TagParser();
		this.pathParser = new PathParser();
	}

	/**
	 * This method runs all parser and generates at the end a swagger file
	 */
	@Override
	public boolean run() throws ApiParserException, IOException {
		try {
			entryPointClassDoc = getEntryPointClassDoc(ParserHelper::hasApplicationPathAnnotation);
			resourceClassDocs = getResources();

			apiParser.setBasicInformation(swagger, entryPointClassDoc);
			tagParser.setTags(swagger);
			pathParser.setPath(swagger);
			definitionParser.setDefinitions(swagger, definitionClassDocs, classDocCache);
			
			writeFile(getFileName(apiParser.getFileName()));

			return true;
		} catch (ApiParserException e) {
			throw new ApiParserException("Error while parsing or searching API entry point!", e);
		} catch (IOException e) {
			throw new IOException("Error while creating or writing the swagger file!", e);
		}
	}

	/**
	 * This method puts all resources in a map and afterwards all subResources 
	 * NOTE: Can't do it like: collect(Collectors.toMap(c -> c, null)) because it throws a
	 * 		 NullPointerException if the value is null 
	 * NOTE: Create temporary hashMap because during iteration you cannot put something into the map
	 */
	private Map<ClassDoc, ClassDoc> getResources() {
		LinkedHashMap<ClassDoc, ClassDoc> tempMap = Arrays.asList(rootDoc.classes()).stream()
				.filter(ParserHelper::hasPathAnnotation)
				.collect(LinkedHashMap::new, (map, classDoc) -> map.put(classDoc, null), HashMap::putAll);

		LinkedHashMap<ClassDoc, ClassDoc> hashMap = new LinkedHashMap<>(tempMap);
		hashMap.entrySet().forEach(c -> getSubs(tempMap, c.getKey()));
		
		return tempMap;
	}

	/**
	 * This method gets all subResources and its subSubResources...
	 */
	private void getSubs(Map<ClassDoc, ClassDoc> tempMap, ClassDoc classDoc) {
		Arrays.asList(classDoc.methods()).stream()
			.filter(methodDoc -> !ParserHelper.isHttpMethod(methodDoc))
			.filter(ParserHelper::hasPathAnnotation)
			.map(methodDoc -> classDocCache.findByType(methodDoc.returnType()))
			.filter(Objects::nonNull)
			.forEach(subClassDoc -> {
					tempMap.put(subClassDoc, classDoc);
					getSubs(tempMap, subClassDoc);
				});
	}
}
