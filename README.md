# xapi-generator

Xapi-Generator is an maven archetype that generates an experience
rest API using Spring Boot and Datasonnet. The headers are pass though meaning the 
exact headers sent in get passed to the downstream API and the headers
that the downstream api responds with also get passed through the response.

The main use case of an experience layer API is the ability to take in 
multiple different payload structures and convert them into a single unified 
structure for the process API. Here, this is accomplished by using datasonnet-mapper.

### Prerequisites

* [Maven](https://maven.apache.org/install.html)  

### Installing

```bash
git clone https://github.com/JakeMHughes/xapi-generator
mvn clean install
```

## Using

To use the archetype, 
```bash
mvn  archetype:generate \
  -DarchetypeGroupId=com.hughesportal \
  -DarchetypeArtifactId=xapi-generator-archetype \
  -DarchetypeVersion=1.0.0-SNAPSHOT \
  -DgroupId=<YOUR_GROUPID> \
  -DartifactId=<YOUR_ARTIFACTID> \
  -Dversion=<YOUR_VERSION> \
  -DOAS=<YOUR_OAS_LOCATION>
```

Additional options:
```bash
  -DdownstreamHost=http://httpbin.org
  -DdownstreamPort=80
  -DdownstreamUrl=/get #This is the base url
```

Example:
```bash
mvn  archetype:generate \
  -DarchetypeGroupId=com.hughesportal \
  -DarchetypeArtifactId=xapi-generator-archetype \
  -DarchetypeVersion=1.0.0-SNAPSHOT \
  -DgroupId=com.test \
  -DartifactId=example \
  -Dversion=1.0.0-SNAPSHOT \
  -DOAS=/home/OAS/example.yaml

```

## After Generation

Look in the generated readme file for the next steps to do after your generated your application.

## Built With

* [Datasonnet](https://github.com/modusbox/datasonnet-mapper) - The data transformation
* [Maven](https://maven.apache.org/) - Dependency Management
* [Spring Boot](https://spring.io/projects/spring-boot) - The Rest API

## Contributing

Anyone is welcome to contribute, but it probably won't be an active project. I recommend creating a fork.
  
## Authors

* **Jacob Hughes** - *Initial work* - [Personal Website](https://hughesportal.com)

## License

This project is licensed under [GPLv3](https://choosealicense.com/licenses/gpl-3.0/)