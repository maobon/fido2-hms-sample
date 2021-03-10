package com.gmrz.fido2.param.model;

public class PublicKeyCredentialUserEntity extends PublicKeyCredentialEntity {
    public String displayName;
    public byte[] id;

    public PublicKeyCredentialUserEntity() {
    }

    public PublicKeyCredentialUserEntity(String displayName, byte[] id) {
        super();
        this.displayName = displayName;
        this.name = displayName;
        this.id = id;
    }
}
