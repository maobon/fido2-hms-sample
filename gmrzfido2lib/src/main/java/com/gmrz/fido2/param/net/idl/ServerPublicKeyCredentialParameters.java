package com.gmrz.fido2.param.net.idl;

public class ServerPublicKeyCredentialParameters {
    private String type;
    private int alg;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAlg() {
        return alg;
    }

    public void setAlg(int alg) {
        this.alg = alg;
    }
}
