# Doclet for generating Swagger from JAX-RS and Javadoc comments
This JavaDoc Doclet is generating a Swagger API documentation of JAX-RS based RESTful Web Services. Additional information that is not contained in JAX-RS annotations is placed in the Javadoc comments.

## Introduction
The normal way of creating a Swagger documentation from Java source code would be <a href="https://github.com/swagger-api/swagger-core">Swagger Core</a> annotations. But there are some issues with them.

First, Swagger annotations are compiled into the JAR / WAR of your server and consume disk space. Second, they don't look good. Third, there are redundant data if you are both documenting your code with Javadoc and using Swagger Annotations, so there is more work for developers to maintain them.

With this doclet it is possible to place Swagger information in the Javadoc comments to generate a Swagger documentation.

## Configuration
Include the plugin to your POM:

```xml
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-javadoc-plugin</artifactId>
	<version>VERSION</version>
	<configuration>
		<doclet>com.spirit21.doclet.Doclet</doclet>
		<docletArtifact>
			<groupId>com.spirit21.doclet</groupId>
			<artifactId>doclet</artifactId>
			<version>VERSION</version>
		</docletArtifact>
		<useStandardDocletOptions>false</useStandardDocletOptions>
	</configuration>
</plugin>
```

## Required Parameters

<table border="1" style="border-collapse: collapse">
	<tr>
		<th>Name:</th>
		<th>Description:</th>
		<th>Default Value:</th>
	</tr>
	<tr>
		<td>outputDirectory</td>
		<td>Specifies the destination directory where this doclet saves the generated Swagger file. User property is: 'destDir'.</td> 
		<td>'${project.build.directory}/apidocs'</td>
	</tr>
	<tr>
		<td>reportOutputDirectory</td>
		<td>Specifies the destination directory where javadoc saves the generated Swagger file.</td>
		<td>'${project.reporting.outputDirectory}/apidocs'</td>
	</tr>
</table>

## Optional Parameters in your configuration-tags

<table border="1" style="border-collapse: collapse">
	<tr>
		<th>Name</th>
		<th>Description</th>
	</tr>
	<tr>
		<td>debug</td>
		<td>Set this to 'true' to debug the Javadoc plugin. With this, javadoc.bat(or.sh), options, @packages or argfile files are provided in the output directory.</td>
	</tr>
	<tr>
		<td>excludePackageNames</td>
		<td>Unconditionally excludes the specified packages and their subpackages from the list formed by -subpackages. Multiple packages can be separated by commas (,), colons (:) or semicolons (;).</td>
	</tr>
	<tr>
		<td>failOnError</td>
		<td>Specifies if the build will fail if there are errors during javadoc execution or not.</td>
	</tr>
</table>

More parameters you can find here: <a href="https://maven.apache.org/plugins/maven-javadoc-plugin/javadoc-mojo.html">More Parameters</a>

## Usage with other dependencies
If you want to include the source files of dependencies, then please include these tags in the configuration:

```xml
<includeDependencySources>true</includeDependencySources>
<dependencySourceIncludes>
	<dependencySourceInclude>GROUP_ID:*</dependencySourceInclude>
	<dependencySourceInclude>...</dependencySourceInclude>
</dependencySourceIncludes>
```

If the source artifact is unavailable, you have to prepare the dependency. You have to put this plugin

```xml
<plugin>
	<artifactId>maven-source-plugin</artifactId>
	<version>2.1.1</version>
   <executions>
   		<execution>
      		<id>bundle-sources</id>
         	<phase>package</phase>
			<goals>
				<!-- produce source artifact for main project sources -->
				<goal>jar-no-fork</goal>
				<!-- produce source artifact for project test sources -->
				<goal>test-jar-no-fork</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```
into your pom.xml of the dependency and execute that project with `mvn clen install` in your command line.

Then you can include the dependency as mentioned above and execute the command `mvn javadoc:aggregate` in your command line.

## Usage without other dependencies
Without other dependencies, your command looks like that: `mvn javadoc:javadoc`.

## Usage
By executing the above mentioned command in the command line, the doclet generates the Swagger file .

### Basic API information
For a valid Swagger documentation, you need to at least provide a title and a version. Place your basic API information in the Javadoc comment of your entry point of your API like in this example:

```java
/**
 * @apiTitle API title
 * @apiVersion version
 * 
 * @apiDescription API description
 * @apiHost localhost:8080
 * @apiBasePath /example/api/v1
 * @fileName swagger-file-name.json
 */
```

### Resources
For defining a resource, a 'Path' annotation must be provided. When using a path parameter, a 'PathParam' annotation must be provided. Here is one example:

```java
@Path("resource/{id}")
public class Resource {
	
	@PathParam("id")
	private Integer id;
	
	private SubResource subResource;
	
	@Path("{secondId}")
	public SubResource getSubResource(@PathParam("secondId") Integer secondId){
		subResource.setSecondId(secondId);
		return subResource;
	}
}
```

```java
public class SubResource {
	...
}
```
	
### Operations
An operation must be defined in the same file as its resource! Otherwise the operation will be ignored because it cannot be assigned to a resource.

The doclet detects the JAX-RS HTTP-method annotations and the `@Produces` / `@Consumes` annotations by itself.

```java

@GET
@Produces("")
@Consumes("")
public void name() {

}

```

### Parameters
Parameters are obtained by the parameter list of the java function. Use the `@QueryParam` annotation for query parameters.
If you want to provide a description for a parameter, use the built-in `@param` Javadoc tag, followed by the name and finally the description. Example:

```java
/**
* ...
* @param i DESCRIPTION
*
*/
public void method(Integer i){
	..
}
```

### Responses
At least one response has to be defined in the Javadoc code. Use the tags `@responseCode` and `@responseMessage`. Provide a schema of your response type with the tag `@responseSchema` followed by a link tag pointing to the class with the schema. Use the tag `@responseType` followed by `array` if an array of object is returned. Following example uses all possible tags:

```java
/**
 * @responseCode 201
 * @responseMessage Types were returned successfully
 * @responseSchema {@link Type}
 * @responseType array
 * 
 * @responseCode 400
 * @responseMessage An error occurred while validation
 * @responseSchema {@link ErrorCode}
 */
```

## Troubleshooting
Following section lists some errors that might occure when executing the doclet and how to fix them.
* "Multiple API entry points found! Only one entry point is allowed."
  * Solution: Delete one API entry point. Only one is allowed.
* "Your API does not have any entry point!"
  * Solution: Create an API entry point with the @ApplicationPath("") - Tag
* "You need to provide information about your API! Version is required."
  * Solution: Add a Javadoc comment block in your entry point class. When already done, then please insert a title.
* "You need to provide information about your API! Title is required."
  * Solution: Add a Javadoc comment block in your entry point class. When already done, then please insert a version.
* "In your documentation of the method METHOD_NAME in CLASS_NAME you have to document at least one response!"
  * Solution: Add at least one response in the Javadoc comment of the method.