package ${groupId}.config.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.openapi4j.core.validation.ValidationException;
import org.openapi4j.operation.validator.adapters.server.servlet.ServletRequest;
import org.openapi4j.operation.validator.model.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import ${groupId}.Application;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor class to validate incoming requests match the OAS
 *
 * @author xapi-generator-archetype
 */
@Component
public class ControllerInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    //This array is for diasabling validation for certain endpoints
    private final String[] skippable = new String[]{
            "/console",
            "/api-docs",
            "/favicon.ico",
            "/swagger-ui",
            "/error"
    };

    //executes before the controller class, returns false on error
    @Override
    public boolean preHandle(HttpServletRequest request,@NotNull HttpServletResponse response,@NotNull Object handler) throws Exception {
        //skip console and Spring related paths
        if(Arrays.stream(skippable).anyMatch(request.getServletPath()::startsWith)) {
            log.info("[Data Validation] Skip");
            return true;
        }

        Request apiSpecRequest = ServletRequest.of(request);


        try {
            //validate the request follows spec
            Application.validator.validate(apiSpecRequest);
            log.info("[Data Validation] Pass");
            return true;
        } catch (ValidationException e) {
            log.info("[Data Validation] Failure");
            Map<String, ArrayList<String>> errorMap = new HashMap<>();
            e.results().items().forEach( item -> {

                //map the validation responses to their crumb
                String reducedMsg, replacedCrumb;
                if(item.dataCrumbs().isEmpty()){
                    replacedCrumb = "other";
                    reducedMsg = item.toString();
                }
                else {
                    reducedMsg = item.toString().substring(item.toString().indexOf(":") + 2, item.toString().indexOf("("));
                    replacedCrumb = item.dataCrumbs().replace("body","payload");
                }

                //create empty value if absent
                errorMap.putIfAbsent(replacedCrumb, new ArrayList<>());
                //append arraylist
                errorMap.get(replacedCrumb).add(reducedMsg);
            });
            //log the errors
            errorMap.forEach( (name,errors) -> log.info(name +": " + String.join(",\n", errors)));

            //map the overall error message
            Map<String, Object> payload = new HashMap<>();
            payload.put("message", "Failed input validation.");
            payload.put("errors", errorMap);
            response.setStatus(400);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write((new ObjectMapper()).writeValueAsString(payload));
            return false;
        }
    }

}