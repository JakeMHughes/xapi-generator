package com.hughesportal.xapigenerator;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TemplateMapper {

    private static Map<String, Object> getObjectProperties(Object bean) {
        try {
            Map<String, Object> map = new HashMap<>();
            Arrays.asList(Introspector.getBeanInfo(bean.getClass(), Object.class)
                    .getPropertyDescriptors())
                    .stream()
                    .filter(pd -> Objects.nonNull(pd.getReadMethod()))
                    .forEach( pd -> {
                        try {
                            Object value = pd.getReadMethod().invoke(bean);
                            map.put(pd.getName(), value);
                        } catch (Exception e) {
                            System.err.println("Filed to invoke method: " + pd.getName());
                            System.err.println(e.toString());
                        }
                    });

            return map;
        } catch (IntrospectionException e) {
            System.err.println("Filed to get bean information.");
            System.err.println(e.toString());
            return Collections.emptyMap();
        }
    }

    public static String mapObject(String template, Object obj){

        String content = template;
        Map<String, Object> values = getObjectProperties(obj);

        for(Map.Entry<String, Object> current : values.entrySet()){
            String value = "";
            if(current.getValue() != null) {
                value = current.getValue().toString();
            }
            else  value = "null";

            content = content.replace("${" + current.getKey() + "}", value);
        }

        return content;
    }

    public static String mapObject(File file, Object obj) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        StringBuilder content= new StringBuilder();

        while((line = br.readLine()) != null){
            content.append(line).append("\n");
        }
        return mapObject(content.toString(), obj);
    }
}
