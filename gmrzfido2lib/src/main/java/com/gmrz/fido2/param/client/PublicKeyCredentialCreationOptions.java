package com.gmrz.fido2.param.client;


import com.gmrz.fido2.param.model.AttestationConveyancePreference;
import com.gmrz.fido2.param.model.AuthenticatorSelectionCriteria;
import com.gmrz.fido2.param.model.PublicKeyCredentialDescriptor;
import com.gmrz.fido2.param.model.PublicKeyCredentialParameters;
import com.gmrz.fido2.param.model.PublicKeyCredentialRpEntity;
import com.gmrz.fido2.param.model.PublicKeyCredentialUserEntity;

import java.util.List;

/**
 * Created by zhangchao on 2018/9/18.
 */

public class PublicKeyCredentialCreationOptions extends RequsetOptions {

    private final byte[] challenge;

    private final PublicKeyCredentialRpEntity rp;

    private final PublicKeyCredentialUserEntity user;

    private final List<PublicKeyCredentialParameters> pubKeyCredParams;

    private final List<PublicKeyCredentialDescriptor> excludeList;

    private final java.util.Map<String,Object> extensions;

    private final AuthenticatorSelectionCriteria authenticatorSelection;

    private final AttestationConveyancePreference attestation;

    private final TokenBinding tokenBinding;

    private final Double timeoutSeconds;

    protected PublicKeyCredentialCreationOptions(byte[] challenge, PublicKeyCredentialRpEntity rp, PublicKeyCredentialUserEntity user, List<PublicKeyCredentialParameters> parameters, List<PublicKeyCredentialDescriptor> excludeList, java.util.Map<String,Object> extensions,
                                                 AuthenticatorSelectionCriteria authenticatorSelection, AttestationConveyancePreference attestation, TokenBinding tokenBinding, Double timeoutSeconds, String origin) {
        this.challenge = challenge;
        this.rp = rp;
        this.user = user;
        this.pubKeyCredParams = parameters;
        this.excludeList = excludeList;
        this.extensions = extensions;
        this.authenticatorSelection = authenticatorSelection;
        this.attestation = attestation;
        this.tokenBinding = tokenBinding;
        this.timeoutSeconds = timeoutSeconds;
        this.origin = origin;
    }

    public byte[] getChallenge() {
        return challenge;
    }

    public PublicKeyCredentialRpEntity getRp() {
        return rp;
    }

    public PublicKeyCredentialUserEntity getUser() {
        return user;
    }

    public List<PublicKeyCredentialParameters> getPubKeyCredParams() {
        return pubKeyCredParams;
    }

    public List<PublicKeyCredentialDescriptor> getExcludeList() {
        return excludeList;
    }

    public java.util.Map<String, Object> getExtensions() {
        return extensions;
    }

    public AuthenticatorSelectionCriteria getAuthenticatorSelection() {
        return authenticatorSelection;
    }

    public AttestationConveyancePreference getAttestation() {
        return attestation;
    }

    public TokenBinding getTokenBinding() {
        return tokenBinding;
    }

    public Double getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public static final class Builder {
        private PublicKeyCredentialRpEntity rpEntity;
        private PublicKeyCredentialUserEntity userEntity;
        private byte[] challenge;
        private List<PublicKeyCredentialParameters> parameters;
        private List<PublicKeyCredentialDescriptor> excludeList;
        private AuthenticatorSelectionCriteria selectionCriteria;
        private AttestationConveyancePreference attestation;
        private java.util.Map<String,Object> extension;
        private TokenBinding tokenBinding;
        private Double timeoutSeconds;
        private String origin;

        public Builder() {
        }

        public final PublicKeyCredentialCreationOptions.Builder setRp(PublicKeyCredentialRpEntity var1) {
            this.rpEntity = var1;
            return this;
        }

        public final PublicKeyCredentialCreationOptions.Builder setUser(PublicKeyCredentialUserEntity var1) {
            this.userEntity = var1;
            return this;
        }

        public final PublicKeyCredentialCreationOptions.Builder setChallenge(byte[] var1) {
            this.challenge = var1;
            return this;
        }

        public final PublicKeyCredentialCreationOptions.Builder setParameters(List<PublicKeyCredentialParameters> var1) {
            this.parameters = var1;
            return this;
        }

        public final PublicKeyCredentialCreationOptions.Builder setExcludeList(List<PublicKeyCredentialDescriptor> var1) {
            this.excludeList = var1;
            return this;
        }

        public final PublicKeyCredentialCreationOptions.Builder setAuthenticatorSelection(AuthenticatorSelectionCriteria var1) {
            this.selectionCriteria = var1;
            return this;
        }

        public final PublicKeyCredentialCreationOptions.Builder setAttestationConveyancePreference(AttestationConveyancePreference var1) {
            this.attestation = var1;
            return this;
        }

        public final PublicKeyCredentialCreationOptions.Builder setAuthenticationExtensions(java.util.Map<String,Object> var1) {
            this.extension = var1;
            return this;
        }

        public final PublicKeyCredentialCreationOptions.Builder setTokenBinding(TokenBinding tokenBinding) {
            this.tokenBinding = tokenBinding;
            return this;
        }

        public PublicKeyCredentialCreationOptions.Builder setTimeoutSeconds(double timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public PublicKeyCredentialCreationOptions.Builder setOrigin(String origin) {
            this.origin = origin;
            return this;
        }

        public final PublicKeyCredentialCreationOptions build() {
            return new PublicKeyCredentialCreationOptions(this.challenge, this.rpEntity, this.userEntity, this.parameters, this.excludeList, this.extension, this.selectionCriteria, this.attestation, this.tokenBinding, this.timeoutSeconds, this.origin);
        }

    }
}
