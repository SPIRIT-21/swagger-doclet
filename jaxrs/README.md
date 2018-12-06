# Doclet for generating Swagger from JAX-RS and Javadoc comments (v0.4)
This JavaDoc Doclet is generating a Swagger API documentation of JAX-RS based RESTful Web Services. Additional information that is not contained in JAX-RS annotations is placed in the Javadoc comments.

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
			<groupId>com.spirit21.swagger</groupId>
			<artifactId>javadoc2swagger</artifactId>
			<version>0.4</version>
		</docletArtifact>
		
		<useStandardDocletOptions>false</useStandardDocletOptions>
		<additionalparam>-backend jaxrs<additionalparam>
	</configuration>
</plugin>
```
You can look up the version of the maven-javadoc-plugin <a href="https://search.maven.org/artifact/org.apache.maven.plugins/maven-javadoc-plugin/3.0.1/maven-plugin">here</a>.
More information about other parameters you can find [here](https://github.com/SPIRIT-21/swagger-doclet/#parameters).

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
 * @fileName swagger-file-name
*/
...
@ApplicationPath("/example/api/v1")
public class CLASSNAME {
	...
}
```

**Important**: Only one entry point of you API is allowed.

### Resources
For defining a resource, a `@Path` annotation must be provided. When using a path parameter, a `@PathParam` annotation must be provided. Here is one example:

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
@Path("resource/{id}/{secondId}")
public class SubResource {
	
	@PathParam("id")
	private Integer id;
	@PathParam("secondId")
	private Integer secondId;
}
```

The swagger tags and the swagger paths will be generated out of the resources.
	
### Operations
If a method in a resource is annotated with a JAX-RS HTTP-method annotation, then this program detects it as a operation.
Following HTTP-Method annotations will be detected: `@GET` / `@POST` / `@PUT` / `@DELETE` / `@HEAD` / `@OPTIONS`

```java
@Path("path/{id}")
public class Resource {
	
	@PathParam("id")
	private Integer id;
	
	/** 
	 * DESCRIPTION
	 * ...
	 */
	@GET
	@Produces("...")
	@Consumes("...")
	public void name() {

	}
	
	/** 
	 * DESCRIPTION
	 * ...
	 */
	@POST
	@Produces("...")
	@Consumes("...")
	public void namePost() {

	}	
	
	/** 
	 * DESCRIPTION
	 * ...
	 */
	@Path("/{secondId}")
	@GET
	@Produces("...")
	@Consumes("...")
	public void name2(@PathParam Integer secondId) {

	}
}
```

Out of this example the doclet will recognize three operations:

<ul>
	<li>GET on path/{id}</li>
	<li>POST on path/{id}</li>
	<li>GET on path/{id}/{secondId}</li> 
</ul> 

The values in the `@Produces` and `@Consumes` will be automatically used for the Swagger specification. 
The description of the method in the javadoc will also be automatically recognized for the Swagger file.

### Parameters
Parameters are obtained by the parameter list of the java function. Use the `@QueryParam` / `@HeaderParam` / `@FormParam` / `@PathParam` annotation for parameters.
If none of them are used the parameter is a BodyParameter. (**Important**: only one BodyParameter is allowed!)
If you want to provide a description for a parameter, use the built-in `@param` Javadoc tag, followed by the name and finally the description. Example:

```java
/**
* ...
* @param i DESCRIPTION
* ...
*/
...
public void method(Integer i){
	..
}
```
The path parameters of an operation in the parameter list of the java function, in the field of the resource class and in its parent resources will be automatically recognized.
**Important**: If a parameter has the `@Context` annotation it will be ignored.

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

**Important**: If a method or a class is annotated with `@Deprecated` it will be ignored!

## Troubleshooting
Following section lists some errors that might occure when executing the doclet and how to fix them.
* "Multiple API entry points found! Only one entry point is allowed."
  * Solution: Only one API entry point is allowed.
* "Your API does not have any entry point!"
  * Solution: Create an API entry point with the @ApplicationPath("") - Tag
* "You need to provide information about your API! Version is required."
  * Solution: Add a Javadoc comment block in your entry point class. When already done, then please insert a title.
* "You need to provide information about your API! Title is required."
  * Solution: Add a Javadoc comment block in your entry point class. When already done, then please insert a version.
* "In your documentation of the method METHOD_NAME in CLASS_NAME you have to document at least one response!"
  * Solution: Add at least one response in the Javadoc comment of the method.