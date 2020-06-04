package com.hughesportal.generated${apiNameCompressed};

import com.datasonnet.Mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.*;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@org.springframework.stereotype.Controller
public class Controller {

    @Autowired
    ResourceLoader resourceLoader;


${endpointsMapping}



    private String executeScript(String scriptFile, String requestBody) throws IOException {
        String script = getResourceValue(resourceLoader.getResource("classpath:" + scriptFile));
        Mapper mapper = new Mapper(script);
        return mapper.transform(requestBody);
    }


    private String getResourceValue(Resource resource) throws IOException {
        Reader reader = new InputStreamReader(resource.getInputStream(), UTF_8);
        return FileCopyUtils.copyToString(reader);
    }
}
