    /**
     * ${summary}
     */${deprecated}
    @${methodCap}Mapping("${path}")
    public ResponseEntity<?> ${name}(
                    @RequestBody(required=false) String requestBody,
                    @RequestHeader Map<String, String> headers,
                    @RequestParam Map<String,String> allParams){

        String mapperType = headers.getOrDefault("Content-Type", "application/json").split("/")[1];
        String linkedParams = getQueryString(allParams);

        if(requestBody == null){
            requestBody = "{}";
        }

        HttpHeaders outHeaders = new HttpHeaders();
        for(String key : headers.keySet()){
            outHeaders.add(key, headers.get(key));
        }

        String queryString = mapHeaders(allParams);
        String headersString = mapHeaders(headers);

        String transformed = mappers.get("${fileName}_IN_" + mapperType + ".ds")
            .transform(new DefaultDocument<>(requestBody, MediaTypes.UNKNOWN),
                Map.of("headers", new DefaultDocument<>(headersString, MediaTypes.APPLICATION_JSON),
                        "params", new DefaultDocument<>(queryString, MediaTypes.APPLICATION_JSON)),
                MediaTypes.ANY).getContent();

        final String uri = "${path}" + linkedParams;

        ResponseEntity<?> result;
        try {
            result = httpRequestSystem
                .getRequestBuilder(webClient, uri)
                .setHeaders(headers)
                .setBody(transformed)
                //.setUrlParameters(List.of(String.valueOf(id)))
                .${method}();
        } catch(WebClientResponseException ex) {
            return ResponseEntity.status(ex.getRawStatusCode()).body(ex.getResponseBodyAsString());
        }

        outHeaders = result.getHeaders();
        outHeaders.remove("Content-Length");

        headersString = mapHeaders(outHeaders.toSingleValueMap());

        return ResponseEntity.status(200).headers(outHeaders).body(mappers.get("${fileName}_OUT_" + mapperType + ".ds")
                            .transform(new DefaultDocument<>(result.getBody(), MediaTypes.UNKNOWN),
                                Map.of("headers", new DefaultDocument<>(headersString, MediaTypes.APPLICATION_JSON),
                                        "params", new DefaultDocument<>(queryString, MediaTypes.APPLICATION_JSON)),
                                 MediaTypes.ANY).getContent());

    }