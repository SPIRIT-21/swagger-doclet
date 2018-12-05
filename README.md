
# Generate Swagger out of REST-API Backends

## Introduction
The normal way of creating Swagger documentation from Java source code would be <a href="https://github.com/swagger-api/swagger-core">Swagger Core</a> annotations. But there are some issues with them.

First, Swagger annotations are compiled into the JAR / WAR of your server and consume disk space. Second, they do not look good. Last, there is much redundant data if you document your code with Javadoc and Swagger annotations. So there is more work for developers.

With this Swagger-Generation-Tool it is possible to place Swagger information into the Javadoc and generate with this information a Swagger documentation.

## Configuration
Include this plugin into the pom.xml of the project:

```xml
<build>
	...
	<plugins>
		...
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-javadoc-plugin</artifactId>
			<version>VERSION</version>
			<configuration>
				<doclet>com.spirit21.Doclet</doclet>
				
				<docletArtifact>
					<groupId>com.spirit21.swagger</groupId>
					<artifactId>javadoc2swagger</artifactId>
					<version>VERSION</version>
				</docletArtifact>
				
				<useStandardDocletOptions>false</useStandardDocletOptions>
				<additionalparam>-backend BACKEND_TYPE</additionalparam>
			</configuration>
		</plugin>
		...
	</plugins>
</build>
```

### Parameters

#### Additional Parameters
These are all parameters and they can be set like it is shown in this example:

<table border="1" style="border-collapse: collapse">
	<tr>
		<th>Name</th>
		<th>Description</th>
		<th>Possible Values</th>
		<th>Default Value</th>
		<th>Required</th>
	</tr>
	<tr>
		<td>&#x2011;backend</td>
		<td>This parameter determines whether it generates the Swagger file for a Spring Boot project or for a JAX-RS project. This parameter is required. When the parameter is invalid or is not set, this tool throws an exception.</td>
		<td>
			<ul>
				<li>spring</li>
				<li>jaxrs</li>
			</ul>
		</td>
		<td>None</td>
		<td>Yes</td>
	</tr>
	<tr>
		<td>-version</td>
		<td>This parameter determines whether the tool should use the Swagger version 2 or 3. This parameter is not required. When the parameter is invalid or is not set, this tool takes the default value.</td>
		<td>
			<ul>
				<li>2</li>
				<li>3</li>
			</ul>
		</td>
		<td>3</td>
		<td>No</td>
	</tr>
	<tr>
		<td>-type</td>
		<td>This parameter determines whether the tool should generate a .yaml or a .json file. This parameter is not required. When the parameter is invalid or is not set, this tool takes the default value.</td>
		<td>
			<ul>
				<li>json</li>
				<li>yaml</li>
			</ul>
		</td>
		<td>json</td>
		<td>No</td>
	</tr>
	<tr>
		<td>&#x2011;filename</td>
		<td>This parameter sets the name of the file which will be generated. If you do not use this parameter this program will use the name out your code at the starting point out of your application (see  <a href="#basic-api-information">below</a>). If there is not any file name given the default value will be used.</td>
		<td>
		Everything you want 		
		</td>
		<td>the value out of your code or 'generated-swagger'</td>
		<td>No</td>
	</tr>
</table>

These parameters can be set like it is shown in this example:

```xml
<additionalparam>-backend spring -type yaml -version 3 -filename openapi</additionalparam>
```

#### Parameters between the configuration-tags
There are several tags you can put between the configuration tags. For example you can specify the output directory.

For more information please read the documentation here: <a href="https://maven.apache.org/plugins/maven-javadoc-plugin/javadoc-mojo.html">Parameters</a>

### Usage

#### Usage with other dependencies
If you want to include the source files of dependencies, then please include these tags between the configuration tags:

```xml
<includeDependencySources>true</includeDependencySources>
<dependencySourceIncludes>
	<dependencySourceInclude>GROUP_ID:*</dependencySourceInclude>
	<dependencySourceInclude>...</dependencySourceInclude>
</dependencySourceIncludes>
```

If the source artifact is unavailable you have to prepare the dependency. You have to put this plugin into the pom.xml of the dependency:

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

Execute this project with `mvn clean install` in the command line.

After this is done you can include the dependency as mentioned above and execute the command `mvn javadoc:aggregate` on the project in the command line.

#### Usage without other dependencies
Without other dependencies you do not need any extra configuration. Just run `mvn javadoc:javadoc` on the project in the command line.

## How this Swagger Generation Tool works
This tool generates a Swagger file by executing the above mentioned command. This tool looks for specific information. Where the information must be located and how the information should look like you can find in the specific project which you find below:

### Swagger Generation out of a JAX-RS Backend
This subproject creates a Swagger file out of a JAX-RS REST-API.

You find more information here: [javadoc-jaxrs-swagger-doclet](https://github.com/SPIRIT-21/swagger-doclet/tree/master/jaxrs)

### Swagger Generation out of a Spring Boot Backend
This subproject creates a Swagger file of a Spring Boot REST-API.

You find more information here: [javadoc-springboot-swagger-doclet](https://github.com/SPIRIT-21/swagger-doclet/tree/master/spring)