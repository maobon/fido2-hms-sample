package com.gmrz.fido2.net.request;

import java.util.HashMap;

public class AttestationResultResponseRequest {

    private String clientDataJSON;
    private String attestationObject;
    private HashMap<String, Object> extensions;
    public String getClientDataJSON() {
        return clientDataJSON;
    }

    public void setClientDataJSON(String clientDataJSON) {
        this.clientDataJSON = clientDataJSON;
    }

    public String getAttestationObject() {
        return attestationObject;
    }

    public void setAttestationObject(String attestationObject) {
        this.attestationObject = attestationObject;
    }

    public HashMap<String, Object> getExtensions() {
        return extensions;
    }

    public void setExtensions(HashMap<String, Object> extensions) {
        this.extensions = extensions;
    }
}
