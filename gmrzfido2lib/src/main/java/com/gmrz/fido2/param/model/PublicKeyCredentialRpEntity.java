package com.gmrz.fido2.param.model;

public class PublicKeyCredentialRpEntity extends PublicKeyCredentialEntity {

    public String id;

    /**
     * @param id
     * @param name
     * @param icon
     */
    public PublicKeyCredentialRpEntity(String id, String name, String icon) {
        super(name, icon);
        this.id = id;
    }

    public PublicKeyCredentialRpEntity() {
        super(null, null);
        this.id = null;
    }
}
