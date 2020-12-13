package ${groupId};

import org.openapi4j.core.exception.ResolutionException;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.validation.RequestValidator;
import org.openapi4j.parser.OpenApi3Parser;
import org.openapi4j.parser.model.v3.OpenApi3;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.URL;

/**
 * Default Spring boot application class
 * Sets the initialization for the OAS validator, and the swagger console
 *
 * @author xapi-generator-archetype
 */
@SpringBootApplication
public class Application {

    static OpenApi3 apiSpec;

    public static RequestValidator validator;

    public static void main(String[] args) throws ResolutionException, ValidationException {
        //Get URL path of file in resources folder
        URL apiFile = Thread.currentThread().getContextClassLoader().getResource("specification.yaml");
        //parse the file to generate OpeanApi3 object
        apiSpec = new OpenApi3Parser().parse(apiFile, true);
        //validator object that handles the validation
        validator = new RequestValidator(Application.apiSpec);

        SpringApplication.run(Application.class, args);
    }

    @Bean
    SpringDocConfiguration springDocConfiguration(){
        return new SpringDocConfiguration();
    }
    @Bean
    public SpringDocConfigProperties springDocConfigProperties() {
        return new SpringDocConfigProperties();
    }
}