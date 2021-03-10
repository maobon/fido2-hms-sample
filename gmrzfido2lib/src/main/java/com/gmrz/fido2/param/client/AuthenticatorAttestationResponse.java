package com.gmrz.fido2.param.client;

public class AuthenticatorAttestationResponse extends AuthenticatorResponse {

    private byte[] credentialId;

    private final byte[] clientDataJson;

    private final byte[] attestationObject;

    private byte[] keyHandle;

    public AuthenticatorAttestationResponse(byte[] clientDataJson, byte[] attestationObject){
        this.clientDataJson = clientDataJson;
        this.attestationObject = attestationObject;
    }

    public AuthenticatorAttestationResponse(byte[] clientDataJson, byte[] attestationObject, byte[] keyHandle){
        this.clientDataJson = clientDataJson;
        this.attestationObject = attestationObject;
        this.keyHandle = keyHandle;
    }

    public byte[] getClientDataJson() {
        return clientDataJson;
    }

    public byte[] getAttestationObject() {
        return attestationObject;
    }

    public byte[] getKeyHandle() {
        return keyHandle;
    }

    public byte[] getCredentialId(){
        return credentialId;
    }
}
