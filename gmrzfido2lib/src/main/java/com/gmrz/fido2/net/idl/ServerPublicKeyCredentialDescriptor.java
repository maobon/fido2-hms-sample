package com.gmrz.fido2.net.idl;

public class ServerPublicKeyCredentialDescriptor {

    private String type;
    private String id;
    private String transports;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransports() {
        return transports;
    }

    public void setTransports(String transports) {
        this.transports = transports;
    }
}
