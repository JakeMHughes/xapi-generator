# xapi-generator

Xapi-Generator is a Java application that generates an experience
rest API using Spring Boot. The headers are pass though meaning the 
exact headers sent in get passed to the downstream API and the headers
that the downstream api responds with also get passed through the response.

The main use cause of an experience layer API is the ability to take in 
multiple different payload structures and convert them into a single unified 
structure for the process API. Here, this is accomplished by using datasonnet-mapper.

## Getting Started

When getting started with xapi-generator you need to have datasonnet-mapper v1.0.5
installed (this currently does not exist in a maven repository).

### Prerequisites

* [Maven](https://maven.apache.org/install.html)  
* [Datasonnet](https://github.com/modusbox/datasonnet-mapper)   

### Installing

```bash
git clone https://github.com/MS3Inc/datasonnet-mapper
mvn clean -Ddockerfile.skip install
git clone https://github.com/JakeMHughes/xapi-generator
mvn clean package
```

## Using

```bash
# Options
# -f <file path to payload>
# -p <String payload>
# Example: 
java -jar target/xapi-generator-1.0-SNAPSHOT-jar-with-dependencies.jar -f examples/httpbin.json
```

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