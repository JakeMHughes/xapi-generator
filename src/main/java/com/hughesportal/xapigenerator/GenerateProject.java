package com.hughesportal.xapigenerator;

import java.io.*;

public class GenerateProject {

    private Xapi xapi;
    private static String tempDir = System.getProperty("java.io.tmpdir");
    public static File mainFolder = new File(tempDir + "/xapi-generator/");

    public GenerateProject(Xapi xapi){
        this.xapi = xapi;
    }

    public void generateProject(){
        try {

            Tools.deleteDir(mainFolder);

            File srcMainDir = new File(tempDir + "/xapi-generator/src/main/java/com/hughesportal/generated" + xapi.getApiNameCompressed() + "/");
            File srcTestDir = new File(tempDir + "/xapi-generator/src/test/java/com/hughesportal/generated" + xapi.getApiNameCompressed() + "/");
            File resDir = new File(tempDir +"/xapi-generator/src/main/resources/");

            if(!mainFolder.mkdirs()){
                System.out.println("Failed to create main directory...");
                System.exit(-1);
            }
            if(!srcMainDir.mkdirs()) {
                errorExit("Failed to create main src directory...");
            }
            if(!srcTestDir.mkdirs()) {
                errorExit("Failed to create test src directory...");
            }
            if(!resDir.mkdirs()) {
                errorExit("Failed to create main resources directory...");
            }

            if(!generateRootFiles(mainFolder)){
                errorExit("Failed to create main resources directory...");
            }

            if(!generateMainSource(srcMainDir)){
                errorExit("Failed to create main source files...");
            }

            if(!generateMainResources(resDir)){
                errorExit("Failed to create resource files...");
            }



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean generateRootFiles(File file){

        System.out.println("Creating file: " + file.getPath() + "/static/pom.xml");
        File pom = new File(file.getPath() +"/pom.xml");

        try{
            System.out.println("Generating pom.xml");
            String content = TemplateMapper.mapObject(Tools.readResourceFile("static/pom.xml"), xapi);
            writeFile(pom, content);
        } catch (IOException e){
            System.out.println("Error: " + e.toString());
            return false;
        }
        return true;
    }

    private boolean generateMainSource(File file){

        File app = new File(file.getPath() +"/Application.java");
        File controller = new File(file.getPath() +"/Controller.java");

        try{
            System.out.println("Generating Application.java...");
            String content = TemplateMapper.mapObject(
                                Tools.readResourceFile("static/Application.java"),
                                xapi);
            writeFile(app, content);


            System.out.println("Generating Controller.java...");
            content = TemplateMapper.mapObject(
                    Tools.readResourceFile("static/Controller.java"),
                    xapi);
            writeFile(controller, content);

        } catch (IOException e){
            System.out.println("Error: " + e.toString());
            return false;
        }
        return true;
    }

    private boolean generateMainResources(File file){
        File props = new File(file.getPath() +"/application.properties");
        try{
            System.out.println("Generating application.properties...");
            StringBuilder content = new StringBuilder();
            content.append("server.port=").append(xapi.getPort()).append("\n");
            content.append("server.servlet.context-path=").append(xapi.getBasePath()).append("\n");

            for(String curr : xapi.getAdditionalProperties()){
                content.append(curr).append("\n");
            }
            writeFile(props, content.toString());

            System.out.println("Generating data sonnet scripts...");
            for(Route endpoint : xapi.getEndpoints()){
                String name = (
                        endpoint.getMethod() +
                                endpoint.getPath()
                                            .replace("/","_")
                                            .replace("-","_")
                                            .replace("{", "uri_")
                                            .replace("}","")
                ).toLowerCase();

                File incomingScript = new File(file.getPath() + "/" + name + "_incoming.ds");
                File outScript = new File(file.getPath() + "/" + name + "_outgoing.ds");

                writeFile(incomingScript, endpoint.getIncomingTransform());
                writeFile(outScript, endpoint.getOutgoingTransform());
            }


        } catch (IOException e){
            System.out.println("Error: " + e.toString());
            return false;
        }
        return true;
    }
/*
    private String replaceValues(String value, Route endpoint){
        String retVal = value
                .replace("${api.name.full}", xapi.getName())
                .replace("${api.name.compressed}", xapi.getName().replaceAll("-", ""));
        if(endpoint != null) {
            String method = endpoint.getMethod();
            String name = (method + endpoint.getPath().replace("/", "_")).toLowerCase();
            retVal = retVal
                .replace("${api.path}", endpoint.getPath())
                .replace("${api.function.name}", name)
                .replace("${api.ds.incoming}", name + "_incoming.ds")
                .replace("${api.ds.outgoing}", name + "_outgoing.ds")
                .replace("${api.url}", endpoint.getApiEndpoint())
                .replace("${api.method.upper}", method.toUpperCase())
                .replace(
                        "${api.method}",
                        (method.substring(0,1).toUpperCase() + method.substring(1).toLowerCase())
                );
        }
        return retVal;
    }


 */

    private void writeFile(File file, String value) throws IOException {
        System.out.println("Writing file: " + file.getPath());
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(value);

        writer.close();
    }

    private void errorExit(String msg){
        System.out.println(msg);
        Tools.deleteDir(mainFolder);
        System.exit(-1);
    }

}
