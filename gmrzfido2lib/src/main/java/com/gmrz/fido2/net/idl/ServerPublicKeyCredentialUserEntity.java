package com.gmrz.fido2.net.idl;

public class ServerPublicKeyCredentialUserEntity extends PublicKeyCredentialEntity {

    private String id;
    private String displayName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}