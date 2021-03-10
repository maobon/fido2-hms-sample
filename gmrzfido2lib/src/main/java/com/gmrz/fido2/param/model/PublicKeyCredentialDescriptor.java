package com.gmrz.fido2.param.model;

import java.util.ArrayList;
import java.util.List;

public class PublicKeyCredentialDescriptor {

    public PublicKeyCredentialType type;
    public byte[] id;
    public List<AuthenticatorTransport> transports;

    public PublicKeyCredentialDescriptor() {
    }

    /**
     * @param type
     * @param id
     */
    public PublicKeyCredentialDescriptor(PublicKeyCredentialType type, byte[] id) {
        this.type = type;
        this.id = id;
        this.transports = new ArrayList<AuthenticatorTransport>();
    }

    /**
     * @param type
     * @param id
     * @param transports
     */
    public PublicKeyCredentialDescriptor(PublicKeyCredentialType type, byte[] id,
                                         ArrayList<AuthenticatorTransport> transports) {
        this.type = type;
        this.id = id;
        this.transports = transports;
    }

}
