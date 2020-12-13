package ${groupId}.controllers;

import com.datasonnet.Mapper;
import com.datasonnet.MapperBuilder;
import com.datasonnet.document.DefaultDocument;
import com.datasonnet.document.MediaTypes;

import ${groupId}.config.servlet.HttpRequestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Spring boot controller class
 *
 * The autowired RequestConfiguration is pulled from the {@link ${groupId}.config.Configurer}
 *
 * @author xapi-generator-archetype
 */
@SuppressWarnings("unused")
@org.springframework.stereotype.Controller
public class Controller {

    @Autowired
    private HttpRequestConfiguration httpRequestSystem;

    private final WebClient webClient;

    @Autowired
    public Controller(){
        this.webClient = (WebClient.builder().build());
    }

${mappers}

${endpointsMapping}


    private String mapHeaders(Map<String, String> headers){

        StringBuilder headerJson= new StringBuilder("{");
        String prefix ="";
        for(String key : headers.keySet()){
            headerJson.append(prefix).append("\"").append(key).append("\": \"").append(headers.get(key)).append("\"");
            prefix=",";
        }
        headerJson.append("}");

        return headerJson.toString();
    }

    private String readDSFile(String filename){
        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream("ds/" + filename)));
        return br.lines().collect(Collectors.joining("\n"));
    }

    private String getQueryString(Map<String,String> allParams){
        return "?" + allParams.entrySet().stream()
                .map( item -> item.getKey()+"="+item.getValue())
                .collect(Collectors.joining("&"));
    }

    /**Future use if headers.remove doesnt work*/
    private HttpHeaders removeContentLength(HttpHeaders headers){
        HttpHeaders temp = new HttpHeaders();
        for( Map.Entry<String, List<String>> entry : headers.entrySet()){
            if(!entry.getKey().equals("Content-Length")) {
                temp.add(entry.getKey(), entry.getValue().get(0));
            }
        }
        return temp;
    }
}
