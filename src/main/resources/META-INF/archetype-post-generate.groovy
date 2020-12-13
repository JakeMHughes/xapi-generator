@Grab(group='io.swagger.parser.v3', module='swagger-parser', version='2.0.21')

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.parser.OpenAPIV3Parser
import org.slf4j.LoggerFactory

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors

def log = LoggerFactory.getLogger("generator.main")

//get file loc from input
String oas = request.properties['OAS']
String groupIdPath = request.groupId

log.info("")
log.info("")
log.info("Setting project paths")
Path root = Paths.get(request.outputDirectory,request.artifactId)
Path packaged = Paths.get(root.toString(), "src/main/java", groupIdPath.replace(".", "/"))
Path resources = Paths.get(root.toString(), "src/main/resources")

log.info("Setting template paths")
Path controllerEndpoint = Paths.get(packaged.toString(), "controllers/ControllerEndpoint.java")
Path controller = Paths.get(packaged.toString(), "controllers/Controller.java")


log.info("Initializing data for processing.")
OpenAPI api = new OpenAPIV3Parser().read(oas)
String endpointTemplate = Files.readString(controllerEndpoint)
ArrayList<String> endpoints = new ArrayList<>()
ArrayList<String> mapperNames = new ArrayList<>()

log.info("Starting to process the OAS.")
api.getPaths().forEach{pathName, pathItem ->
    String[] methodList = ["Get", "Post", "Patch", "Put", "Delete"]

    log.info("Processing path: " + pathName)
    for(String currentMethod : methodList){
        Operation op = null;
        if(currentMethod.equals("Get")){ op = pathItem.getGet() }
        else if(currentMethod.equals("Post")){ op = pathItem.getPost() }
        else if(currentMethod.equals("Patch")){ op = pathItem.getPatch() }
        else if(currentMethod.equals("Put")){ op = pathItem.getPut() }
        else if(currentMethod.equals("Delete")){ op = pathItem.getDelete() }


        if(op != null) {
            log.info("Running op: " + currentMethod)
            ArrayList<String> currentMapperNames = generateMapper(pathName, op, currentMethod, resources)
            if (currentMapperNames != null) {
                mapperNames.addAll(currentMapperNames)
            }
            String endpoint = generateEndpoint(endpointTemplate, pathName, op, currentMethod, currentMapperNames)
            if (endpoint != null) {
                endpoints.add(endpoint)
            }
        }
    }
    log.info("Finished path...")
}

String controllerTemplate = Files.readString(controller);

log.info("")
log.info("")
log.info("Finalizing the data")
String allEndpoints = endpoints.stream().collect(Collectors.joining("\n"))
String mappersInit = generateMappersInit(mapperNames)

log.info("Writing the Controllers class")
Files.write(controller,
            controllerTemplate
                .replace("\${mappers}", mappersInit)
                .replace("\${endpointsMapping}", allEndpoints)
                    .getBytes());

log.info("Deleting the endpoint template.")
Files.delete(controllerEndpoint);
log.info("Saving the specification to resources")
Files.copy(Paths.get(oas),Paths.get(resources.toString(), "specification.yaml"));




/****************Functions Decalarations********************/

static def generateMappersInit(ArrayList<String> mappers){
    String data = mappers.stream().map {
        "\t\tnew AbstractMap.SimpleEntry<>(" +
        wrap(it) +", new MapperBuilder(readDSFile(" +
        wrap(it) + ")).withInputNames(\"headers\", \"params\").build())"
    }.collect(Collectors.joining(",\n"))

    return "\tprivate final Map<String, Mapper> mappers = Map.ofEntries(\n" +
                data + "\n\t);"
}

static def generateEndpoint(String template, String path, Operation op, String method, ArrayList<String> mapperNames){
    if(op == null){
        return null
    }
    String  summary=op.getSummary(),
            deprecated="",
            name=nameGeneration(path,method)
    if(op.getDeprecated() != null ){
        deprecated = "\n\t@Deprecated"
    }
    if(summary == null){
        summary=op.getDescription()
    }


    //fill in the template file
    return template
            .replace("\${summary}", summary)
            .replace("\${deprecated}", deprecated)
            .replaceAll("[\$][{]path[}]", path)
            .replaceAll("[\$][{]fileName}", "ds/"+name)
            .replace("\${methodCap}", method)
            .replace("\${method}", method.toLowerCase())
            .replace("\${name}", name)
}

static def generateMapper(String path, Operation op, String method, Path resources){
    if(op == null){ return null }
    def log = LoggerFactory.getLogger("generator.generateMapper")

    ArrayList<String> names = new ArrayList<>()
    String name = nameGeneration(path, method)

    //Input section, since currently every method gets an input map,
    //had to create a default mapping file
    if(op.getRequestBody() != null){
        op.getRequestBody().getContent().forEach{ str, mediaType ->
            String fileName = writeScript(str,name, "IN", resources)
            names.add(fileName)
        }
    }else{ //create default file
        String fileName = writeScript("application/json",name, "IN", resources)
        names.add(fileName)
    }

    //output section, first needs to get the valid response
    // then gets the content of the valid response
    if(op.getResponses() != null){
        String responseCode =
                op.getResponses().keySet().stream()
                    .filter{it.startsWith("2")}
                    .findFirst().get()
        Content content = op.getResponses().get(responseCode).getContent()
        if(content != null) {
            content.forEach { str, mediaType ->
                    String fileName = writeScript(str, name, "OUT", resources)
                    names.add(fileName)
                }
        }
    }

    log.info("Generated I/O datasonnet scripts")

    return names;
}

static def writeScript(String str, String name, String type, Path resources){
    Path dir = Paths.get(resources.toString(), "ds")
    String fileName = name + "_" + type + "_" +
            str.split("/")[1] + ".ds"
    String header =
            "/** Datasonnet\n" +
                    "version=2.0\n" +
                    "output " + str + "\n" +
                    "input payload " + str + "\n" +
                    "*/\n"
    String body = "payload"
    //TODO set body using example


    // create ds directory
    Files.createDirectories(dir)
    //write the DS script file
    Files.write(Paths.get(dir.toString(), fileName), (header+body).getBytes())
    return fileName
}

static def nameGeneration(String path, String method){
    return method.toLowerCase() +
            path.replaceAll("[{}]", "")
                    .replaceAll("[/]", "_")
}

static def wrap(String str){
    return "\"" + str + "\""
}