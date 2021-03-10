package com.gmrz.fido2.param.model;

public enum PublicKeyCredentialType {
    PUBLIC_KEY("public-key");
    String name;

    /**
     * @param name
     */
    PublicKeyCredentialType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
