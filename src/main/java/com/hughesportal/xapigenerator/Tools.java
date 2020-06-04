package com.hughesportal.xapigenerator;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

public class Tools {

    public static Xapi mapPayload(String payload, File file) {

        Xapi xapi = null;
        try {
            if (file != null && file.exists()) {
                ObjectMapper mapper = new ObjectMapper();
                xapi = mapper.readValue(file, Xapi.class);
            } else {
                ObjectMapper mapper = new ObjectMapper();
                xapi = mapper.readValue(payload, Xapi.class);
            }
        } catch (IOException ex){
            System.out.println("Failed to map the payload...");
            System.out.println("Error: " + ex.toString());
            System.exit(-1);
        }

        return xapi;
    }


    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static String readResourceFile(String fileName) throws IOException {
        StringBuilder content = new StringBuilder();
        File file = new File(Tools.class.getClassLoader().getResource(fileName).getFile());

        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        while ((line = br.readLine()) != null){
            content.append(line).append("\n");
        }

        br.close();

        return content.toString();
    }

}

