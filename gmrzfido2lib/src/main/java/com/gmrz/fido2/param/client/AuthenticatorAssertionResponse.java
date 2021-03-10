package com.gmrz.fido2.param.client;

public class AuthenticatorAssertionResponse extends AuthenticatorResponse {

    private byte[] keyHandle;

    private final byte[] clientDataJson;

    private final byte[] authenticatorData;

    private final byte[] signature;

    private final byte[] credentialId;

    private byte[] userHandle;

    public AuthenticatorAssertionResponse(byte[] clientDataJson,byte[] authenticatorData,byte[] signature,byte[] credentialId){
        this.clientDataJson = clientDataJson;
        this.authenticatorData = authenticatorData;
        this.signature = signature;
        this.credentialId = credentialId;
    }

    public AuthenticatorAssertionResponse(byte[] clientDataJson,byte[] authenticatorData,byte[] signature,byte[] credentialId,byte[] userHandle,byte[] keyHandle){
        this.clientDataJson = clientDataJson;
        this.authenticatorData = authenticatorData;
        this.signature = signature;
        this.credentialId = credentialId;
        this.userHandle = userHandle;
        this.keyHandle = keyHandle;
    }

    public byte[] getClientDataJson() {
        return clientDataJson;
    }

    public byte[] getAuthenticatorData() {
        return authenticatorData;
    }

    public byte[] getSignature() {
        return signature;
    }

    public byte[] getCredentialId() {
        return credentialId;
    }

    public byte[] getKeyHandle() {
        return keyHandle;
    }

    public byte[] getUserHandle() {
        return userHandle;
    }
}
