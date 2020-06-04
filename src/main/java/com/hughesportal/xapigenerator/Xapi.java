package com.hughesportal.xapigenerator;

import java.io.IOException;
import java.util.ArrayList;

public class Xapi {

    private ArrayList<Route> endpoints;
    private String basePath;
    private String port = "8080";
    private String name = "xapi";
    private ArrayList<String> additionalProperties;

    public void setEndpoints(ArrayList<Route> endpoints) { this.endpoints = endpoints; }

    public void setBasePath(String basePath) { this.basePath = basePath; }

    public void setPort(String port) { this.port = port; }

    public void setName(String name) { this.name = name; }

    public void setAdditionalProperties(ArrayList<String> additionalProperties) { this.additionalProperties = additionalProperties; }

    public String getName() { return name; }

    public ArrayList<Route> getEndpoints() { return endpoints; }

    public String getPort(){return port;}

    public String getBasePath(){return basePath;}

    public ArrayList<String> getAdditionalProperties(){return additionalProperties;}


    public String getApiNameCompressed(){
        return name.replaceAll("-", "");
    }

    public String getEndpointsMapping() throws IOException {
        StringBuilder content = new StringBuilder().append("");
        for(Route endpoint: endpoints){
            content.append(endpoint.toString());
        }
        return content.toString();
    }

    public boolean validateObject(){
        if(name.contains(" ")) {return false;}

        if(basePath.contains(" ")) {return false;}

        if(Integer.parseInt(port)<=0) {return false;}

        ArrayList<String> names = new ArrayList<>();
        for(Route endpoint : endpoints){
            if(!endpoint.getPath().startsWith("/") || endpoint.getPath().contains("\\")) {return  false;}

            String name = (endpoint.getMethod() + endpoint.getPath().replace("/", "_")).toLowerCase();

            if(names.contains(name)) {return false;}
            else {names.add(name);}
        }

        return true;
    }
}
