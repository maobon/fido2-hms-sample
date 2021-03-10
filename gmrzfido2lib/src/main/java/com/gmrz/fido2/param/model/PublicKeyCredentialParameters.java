package com.gmrz.fido2.param.model;

public class PublicKeyCredentialParameters {

    public PublicKeyCredentialType type;
    public Algorithm alg;

    public PublicKeyCredentialParameters() {
    }

    /**
     * @param type
     * @param algorithm
     */
    public PublicKeyCredentialParameters(PublicKeyCredentialType type, Algorithm algorithm) {
        this.type = type;
        this.alg = algorithm;
    }
}
