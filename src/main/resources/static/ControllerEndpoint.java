    @${methodCap}Mapping("${path}")
    public ResponseEntity<?> ${name}(@RequestBody String requestBody,
                                     @RequestHeader Map<String, String> headers){
        String transfomredJson;
        try{
            transfomredJson = executeScript("${dsIncoming}", requestBody);
        } catch (IOException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.toString());
        }

        final String uri = "${url}";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders outHeaders = new HttpHeaders();
        for(String key : headers.keySet()){
            outHeaders.add(key, headers.get(key));
        }

        ResponseEntity result = restTemplate.exchange(uri, HttpMethod.${methodUpper}, new HttpEntity<>(transfomredJson, outHeaders), String.class);

        try{
            transfomredJson = executeScript("${dsOutgoing}", result.getBody().toString());
        } catch (IOException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.toString());
        }

        outHeaders = new HttpHeaders();
        for( Map.Entry<String, List<String>> entry : result.getHeaders().entrySet()){
            if(!entry.getKey().equals("Content-Length")) {
                System.out.println(entry.getKey() + ": " + entry.getValue().get(0));
                outHeaders.add(entry.getKey(), entry.getValue().get(0));
            }
        }

        return ResponseEntity.ok()
                    .headers(outHeaders)
                    .body(transfomredJson);
    }