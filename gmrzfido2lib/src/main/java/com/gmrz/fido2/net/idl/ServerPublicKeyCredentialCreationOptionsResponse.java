package com.gmrz.fido2.net.idl;


import com.huawei.hms.support.api.fido.fido2.AuthenticatorSelectionCriteria;

import java.util.HashMap;

public class ServerPublicKeyCredentialCreationOptionsResponse extends ServerResponse {

    private PublicKeyCredentialRpEntity rp;
    private ServerPublicKeyCredentialUserEntity user;
    private String challenge;
    private ServerPublicKeyCredentialParameters[] pubKeyCredParams;
    private long timeout;
    private ServerPublicKeyCredentialDescriptor[] excludeCredentials;
    private AuthenticatorSelectionCriteria authenticatorSelection;
    private String attestation;
    private HashMap<String, Object> extensions;

    private String rpId;
    private ServerPublicKeyCredentialDescriptor[] allowCredentials;
    private String userVerification;

    public PublicKeyCredentialRpEntity getRp() {
        return rp;
    }

    public void setRp(PublicKeyCredentialRpEntity rp) {
        this.rp = rp;
    }

    public ServerPublicKeyCredentialUserEntity getUser() {
        return user;
    }

    public void setUser(ServerPublicKeyCredentialUserEntity user) {
        this.user = user;
    }

    public String getChallenge() {
        return challenge;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public ServerPublicKeyCredentialParameters[] getPubKeyCredParams() {
        return pubKeyCredParams;
    }

    public void setPubKeyCredParams(ServerPublicKeyCredentialParameters[] pubKeyCredParams) {
        this.pubKeyCredParams = pubKeyCredParams;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public ServerPublicKeyCredentialDescriptor[] getExcludeCredentials() {
        return excludeCredentials;
    }

    public void setExcludeCredentials(ServerPublicKeyCredentialDescriptor[] excludeCredentials) {
        this.excludeCredentials = excludeCredentials;
    }

    public AuthenticatorSelectionCriteria getAuthenticatorSelection() {
        return authenticatorSelection;
    }

    public void setAuthenticatorSelection(AuthenticatorSelectionCriteria authenticatorSelection) {
        this.authenticatorSelection = authenticatorSelection;
    }

    public String getAttestation() {
        return attestation;
    }

    public void setAttestation(String attestation) {
        this.attestation = attestation;
    }

    public HashMap getExtensions() {
        return extensions;
    }

    public void setExtensions(HashMap extensions) {
        this.extensions = extensions;
    }

    public String getRpId() {
        return rpId;
    }

    public void setRpId(String rpId) {
        this.rpId = rpId;
    }

    public ServerPublicKeyCredentialDescriptor[] getAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(ServerPublicKeyCredentialDescriptor[] allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public String getUserVerification() {
        return userVerification;
    }

    public void setUserVerification(String userVerification) {
        this.userVerification = userVerification;
    }
}
