package com.hughesportal.xapigenerator;

import java.io.IOException;

public class Route {

    private String path;
    private String method;
    private String incomingTransform ="payload";
    private String outgoingTransform ="payload";
    private String url;


    public Route(){
        path = null;
        method = null;
        url = null;
    }

    public Route(String path, String method, String incomingTransform, String outgoingTransform, String apiEndpoint) {
        this.path = path;
        this.method = method;
        this.incomingTransform = incomingTransform;
        this.outgoingTransform = outgoingTransform;
        this.url = apiEndpoint;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getIncomingTransform() {
        return incomingTransform;
    }

    public void setIncomingTransform(String incomingTransform) {
        this.incomingTransform = incomingTransform;
    }

    public String getOutgoingTransform() {
        return outgoingTransform;
    }

    public void setOutgoingTransform(String outgoingTransform) {
        this.outgoingTransform = outgoingTransform;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String apiEndpoint) {
        this.url = apiEndpoint;
    }


    public String getName(){
        return (method + path.replace("/", "_").replace("-", "_")).toLowerCase();
    }

    public String getDsIncoming(){
        return getName() + "_incoming.ds";
    }

    public String getDsOutgoing(){
        return getName() + "_outgoing.ds";
    }

    public String getMethodUpper(){ return method.toUpperCase();}

    public String getMethodCap(){
        return (method.substring(0,1).toUpperCase() + method.substring(1).toLowerCase());
    }


    @Override
    public String toString() {
        try {
            return TemplateMapper.mapObject(
                    Tools.readResourceFile("static/ControllerEndpoint.java"),
                    this
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
