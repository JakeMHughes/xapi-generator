package com.hughesportal.generated${apiNameCompressed};

import com.datasonnet.Mapper;
import com.datasonnet.document.Document;
import com.datasonnet.document.StringDocument;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

@org.springframework.stereotype.Controller
public class Controller {

    @Autowired
    ResourceLoader resourceLoader;


${endpointsMapping}



    private String executeScript(String scriptFile, String requestBody, String headers, String queryParameters) throws IOException {

        String script = getResourceValue(resourceLoader.getResource("classpath:" + scriptFile));

        if(headers != null){
            Map<String, Document> headerKey = new HashMap<>();
            headerKey.put("headers", new StringDocument(headers, "application/json"));
            headerKey.put("queryParams", new StringDocument(queryParameters, "application/json"));
            Mapper mapper = new Mapper(script, headerKey.keySet(), true);
            return mapper.transform(new StringDocument(requestBody, "application/json"), headerKey, "application/json").getContentsAsString();
        }else{
            Mapper mapper = new Mapper(script);
            return mapper.transform(requestBody);
        }
    }


    private String getResourceValue(Resource resource) throws IOException {
        Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8);
        return FileCopyUtils.copyToString(reader);
    }

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

}
