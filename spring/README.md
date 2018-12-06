# Doclet for generating Swagger from Spring Boot and Javadoc comments
This JavaDoc Doclet is generating a Swagger API documentation of Spring Boot RESTful Web Services. Additional information that is not contained in Spring Boot annotations is placed in the Javadoc comments.

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
			<version>VERSION</version>
		</docletArtifact>
		
		<useStandardDocletOptions>false</useStandardDocletOptions>
		<additionalparam>-backend spring<additionalparam>
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
@SpringBootApplication
public class CLASSNAME {
	...
}
```

**Important**: Only one entry point of you API is allowed.

### Resources
For defining a resource, a `@RestController` or a `@Controller` annotation must be provided. To map this resource to a path the `@RequestMapping(..)` annotation must be provided. When using a path parameter, a `@PathVariable` annotation must be provided. Here is one example:

```java
@RestController
@RequestMapping("resource/{id}")
public class Resource {
	
	@RequestMapping("/{secondId}")
	public void test(@PathVariable("secondId") Integer secondId){
		..
	}
}
```

```java
@Controller
@RequestMapping("resource/{id}/{secondId}")
public class SubResource {
	
	@GetMapping
	public void test(){
		..
	}
}
```

The swagger tags and the swagger paths will be generated out of the resources.
	
### Operations
If a method in a resource is annotated with a Spring Boot HTTP-method annotation, then this program detects it as a operation.
Following HTTP-Method annotations will be detected: `@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping` / `@PatchMapping` / `@RequestMapping(method = {RequestMethod.GET/HEAD/POST/PUT/PATCH/DELETE/OPTIONS})`

```java
@Controller
@RequestMapping("path/{id}")
public class Resource {
	
	/** 
	 * DESCRIPTION
	 * ...
	 */
	@RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, produces = {..}, consumes = {..})
	public void name() {

	}
	
	/** 
	 * DESCRIPTION
	 * ...
	 */
	@PostMapping(path = {..}, produces = {..}, consumes = {..})
	public void namePost() {

	}	
	
	/** 
	 * DESCRIPTION
	 * ...
	 */
	@GetMapping("/{secondId}")
	public void name2(@PathVariable Integer secondId) {

	}
}
```

Out of this example the doclet will recognize these operations:

<ul>
	<li>GET on path/{id}/{secondId}</li>
	<li>GET and POST on path/{id}</li>
	<li>POST on path/{id}/...</li> 
</ul> 

The produces and consumes values in the `@Get/Post/Put/Delete/PatchMapping` and `@RequestMapping` annotations will be automatically used for the Swagger specification. 
The description of the method in the javadoc will also be automatically recognized for the Swagger file.<br/>
**Important**: There are multiple paths in the mapping annotation possible. For all of those paths will be an operation generated.  

### Parameters
Parameters are obtained by the parameter list of the java function. Use the `@RequestBody` / `@RequestHeader` / `@RequestParam` / `@PathVariable` annotation for parameters.
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