package ${groupId}.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class Console {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //this endpoint is for springdoc to parse our specification for the console
    @GetMapping(value = "/api-docs")
    public ResponseEntity<?> getSwaggerDoc() throws IOException {
        String value = getFileFromResources("specification.yaml");
        return ResponseEntity.status(200).body(value);
    }

    //In my use case, we used Kong as a proxy, at the time ( and maybe still) there
    // was no way to modify some of the forward headers spring doc requires so I created this
    //loop back function to negate the redirect
    @GetMapping(value = "/console")
    public ResponseEntity<?> getSwaggerConsole() throws Exception {
        WebClient webClient = WebClient.create("http://localhost:8080");
        String body;
        try {
            body = webClient.get()
                    //my spring doc endpoint
                    .uri("/api/swagger-ui/index.html?configUrl=/api/api-docs/swagger-config")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            //this fixes some location issues in the JavaScript
            body = body.replaceAll("src=\".", "src=\"./swagger-ui")
                    .replaceAll("href=\".", "href=\"./swagger-ui")
                    .replace("url: \"https://petstore.swagger.io/v2/swagger.json\",", "url: \"/api/api-docs\",");
            return ResponseEntity.ok(Objects.requireNonNull(body));
        } catch (WebClientResponseException ex){
            log.error(ex.getLocalizedMessage());
            throw new Exception();
        }
    }

    private String getFileFromResources(String fileName) throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new ClassPathResource(fileName).getInputStream()));
        return reader.lines().collect(Collectors.joining("\n"));
    }

}