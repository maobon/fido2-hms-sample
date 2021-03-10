package com.gmrz.fido2.param.net.request;

import java.util.HashMap;

public class AssertionResultRequest {

    private String id;
    private String rawId;
    private AssertionResultResponseRequest response;
    private String type;
    private HashMap<String, Object> extensions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRawId() {
        return rawId;
    }

    public void setRawId(String rawId) {
        this.rawId = rawId;
    }

    public AssertionResultResponseRequest getResponse() {
        return response;
    }

    public void setResponse(AssertionResultResponseRequest response) {
        this.response = response;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, Object> getExtensions() {
        return extensions;
    }

    public void setExtensions(HashMap<String, Object> extensions) {
        this.extensions = extensions;
    }
}
