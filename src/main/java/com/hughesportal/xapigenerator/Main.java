package com.hughesportal.xapigenerator;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Main {

    private static Xapi xapi = null;

    public static void main(String[] args) throws JsonProcessingException {


        //initial args read
        argsHandler(args);

        //validate payload
        if(!xapi.validateObject()){
            System.out.println("Failed to validate object...");
            System.exit(-1);
        }

        //generate project files
        GenerateProject gp = new GenerateProject(xapi);
        gp.generateProject();

        //zip the project files TODO

        //delete local files

        //Initialize.deleteDir(Initialize.mainFolder);
    }


    private static void argsHandler(String[] args){
        //-p inline payload
        //-f file location
        if(args.length>=2 && args[0].equals("-p")){
            String payload ="";
            for(int i =1; i < args.length ; i ++){
                payload += args[i];
            }
            xapi = Tools.mapPayload(payload, null);
        }
        else if(args.length>=2 &&args[0].equals("-f")){
            File file = new File(args[1]);
            xapi = Tools.mapPayload(null, file);
        }
        else{
            System.out.println("java -jar xapi-generator.jar <OPTION> <VALUE>\n");
            System.out.println("OPTIONS");
            System.out.println("==========================================================================================");
            System.out.println("-p \t->\tA Payload value in the form of a JSON string is required");
            System.out.println("-f \t->\tA file location containing the JSON value is required");
            System.exit(-1);
        }
    }

}
