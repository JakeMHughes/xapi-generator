package ${groupId}.config;

import com.test.config.servlet.HttpRequestConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring boot Configuration that configures the HttpRequest classes for autowiring
 *
 * @author xapi-generator-archetype
 */
@SuppressWarnings("unused")
@Configuration
public class Configurer {

    /**
        This is an example configuration. it uses the
        downstream.system properties in the `application.properties`
        file to generate the HttpRequest class.
        Copy this snippet for each api you need to connect to.
        When you want to use it, you would do:

        //@Autowired
        //HttpRequestConfiguration httpRequestSystem

        When autowiring, you must use the same name.

    */
    @Bean
    @ConfigurationProperties("downstream.system")
    public HttpRequestConfiguration httpRequestSystem(){
        return new HttpRequestConfiguration();
    }

}