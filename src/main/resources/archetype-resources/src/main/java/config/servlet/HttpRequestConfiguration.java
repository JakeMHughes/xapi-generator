package ${groupId}.config.servlet;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * HttpRequestConfiguration makes it easy to generate HTTP request
 *
 * @author xapi-generator-archetype
 */
@SuppressWarnings("unused")
@Configuration
@ConfigurationProperties("downstream.properties") //default properties variable
public class HttpRequestConfiguration {

    private String hostname;
    private String port;
    private String baseurl;
    private String apiUrl;

    public HttpRequestConfiguration(){}

    public HttpRequestConfiguration(String hostname, String port, String baseurl) {
        this.hostname = hostname;
        this.port = port;
        this.baseurl = baseurl;
        this.apiUrl = hostname+":" +port+""+baseurl;
    }


    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getBaseurl() {
        return baseurl;
    }

    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    public String getApiUrl(){
        if(apiUrl == null){
            this.apiUrl = hostname+":" +port+""+baseurl;
        }
        return apiUrl;
    }

    public RequestBuilder getRequestBuilder(WebClient webClient, String urlPath){
        return new RequestBuilder(webClient, getApiUrl(), urlPath);
    }


    public static class RequestBuilder {

        private HttpMethod method;
        private WebClient webClient;
        private Class<?> clazz = Object.class;
        private String urlPath;
        private Object payload = "";
        private List<String> urlParams = List.of();
        private Map<String, String> headers = Map.of();
        private String baseUrl;

        private RequestBuilder() { }

        public RequestBuilder(WebClient webClient, String baseUrl, String urlPath) {
            this.webClient = webClient;
            this.baseUrl = baseUrl;
            this.urlPath = urlPath;
        }

        public RequestBuilder setMethod (HttpMethod method){
            this.method = method;
            return this;
        }

        public RequestBuilder setMethod (String method){
            this.method = HttpMethod.resolve(method);
            return this;
        }

        public RequestBuilder setClass (Class < ? > clazz){
            this.clazz = clazz;
            return this;
        }

        public RequestBuilder setUrlPath (String urlPath){
            this.urlPath = urlPath;
            return this;
        }

        public RequestBuilder setBody (Object payload){
            this.payload = payload;
            return this;
        }

        public RequestBuilder setHeaders (Map < String, String > headers){
            this.headers = headers;
            return this;
        }

        public RequestBuilder setUrlParameters (List < String > urlParams) {
            this.urlParams = urlParams;
            return this;
        }

        public ResponseEntity<?> get() throws WebClientResponseException {
            this.method = HttpMethod.GET;
            return execute();
        }

        public ResponseEntity<?> post() throws WebClientResponseException {
            this.method = HttpMethod.POST;
            return execute();
        }

        public ResponseEntity<?> patch() throws WebClientResponseException {
            this.method = HttpMethod.PATCH;
            return execute();
        }

        public ResponseEntity<?> put() throws WebClientResponseException {
            this.method = HttpMethod.PUT;
            return execute();
        }
        public ResponseEntity<?> delete() throws WebClientResponseException {
            this.method = HttpMethod.DELETE;
            return execute();
        }

        private ResponseEntity<?> execute () throws WebClientResponseException {
            return webClient.method(method)
                    .uri(baseUrl + urlPath, urlParams.toArray())
                    .headers(httpHeaders -> httpHeaders.setAll(Objects.requireNonNullElse(headers, Map.of())))
                    .body(Mono.just(payload), String.class).retrieve()
                    .toEntity(clazz).block();
        }

    }


}