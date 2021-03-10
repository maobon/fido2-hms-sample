package com.gmrz.fido2.param.model;


public class PublicKeyCredentialEntity {
    public String name;
    public String icon;

    /**
     * @param name
     * @param icon
     */
    PublicKeyCredentialEntity(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    PublicKeyCredentialEntity() {
        this.name = null;
        this.icon = null;
    }

}
