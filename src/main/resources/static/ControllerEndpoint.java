    @${methodCap}Mapping("${path}")
    public ResponseEntity<?> ${name}(@RequestBody(required=false) String requestBody, @RequestHeader Map<String, String> headers, @RequestParam Map<String,String> allParams){

        String linkedParams = "?" + allParams.entrySet()
                            .stream()
                            .map( item -> item.getKey()+"="+item.getValue())
                            .collect(Collectors.joining("&"));

        if(requestBody == null){
            requestBody = "{}";
        }

        HttpHeaders outHeaders = new HttpHeaders();
        for(String key : headers.keySet()){
            outHeaders.add(key, headers.get(key));
        }

        String queryString = mapHeaders(allParams);
        String headersString = mapHeaders(headers);

        String transfomredJson;
        try{
            transfomredJson = executeScript("${dsIncoming}", requestBody, headersString,queryString);
        } catch (IOException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.toString());
        }

        final String uri = "${url}" + linkedParams;
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity result = restTemplate.exchange(uri, HttpMethod.${methodUpper}, new HttpEntity<>(transfomredJson, outHeaders), String.class);

        outHeaders = new HttpHeaders();
        for( Map.Entry<String, List<String>> entry : result.getHeaders().entrySet()){
            if(!entry.getKey().equals("Content-Length")) {
                System.out.println(entry.getKey() + ": " + entry.getValue().get(0));
                outHeaders.add(entry.getKey(), entry.getValue().get(0));
            }
        }

        headersString = mapHeaders(outHeaders.toSingleValueMap());

        try{
            transfomredJson = executeScript("${dsOutgoing}", result.getBody().toString(), headersString, "{}");
        } catch (IOException ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.toString());
        }


        return ResponseEntity.ok()
                    .headers(outHeaders)
                    .body(transfomredJson);
    }