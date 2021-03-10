package com.gmrz.fido2.param.client;


import com.gmrz.fido2.param.model.PublicKeyCredentialDescriptor;

import java.util.List;
import java.util.Map;

/**
 * Created by zhangchao on 2018/9/18.
 */

public class PublicKeyCredentialRequestOptions extends RequsetOptions {

    private final String rpId;

    private final byte[] challenge;

    private final List<PublicKeyCredentialDescriptor> allowList;

    private final Map<String,Object> extensions;

    private final Double timeoutSeconds;

    private final TokenBinding tokenBinding;

    public String getRpId() {
        return rpId;
    }

    public byte[] getChallenge() {
        return challenge;
    }

    public List<PublicKeyCredentialDescriptor> getAllowList() {
        return allowList;
    }

    public Map<String, Object> getExtensions() {
        return extensions;
    }

    public Double getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public TokenBinding getTokenBinding() {
        return tokenBinding;
    }

    public PublicKeyCredentialRequestOptions(byte[] challenge, String rpId, List<PublicKeyCredentialDescriptor> allowList, Map<String,Object> extensions,
                                             Double timeoutSeconds, TokenBinding tokenBinding, String origin){
        this.challenge = challenge;
        this.rpId = rpId;
        this.allowList = allowList;
        this.extensions = extensions;
        this.timeoutSeconds = timeoutSeconds;
        this.tokenBinding = tokenBinding;
        this.origin = origin;
    }

    public static final class Builder {
        private byte[] challenge;
        private String rpId;
        private List<PublicKeyCredentialDescriptor> allowList;
        private Map<String,Object> extensions;
        private Double timeoutSeconds;
        private TokenBinding tokenBinding;
        private String origin;

        public Builder() {
        }

        public final PublicKeyCredentialRequestOptions.Builder setChallenge(byte[] var1) {
            this.challenge = var1;
            return this;
        }

        public final PublicKeyCredentialRequestOptions.Builder setRpId(String var1) {
            this.rpId = var1;
            return this;
        }

        public final PublicKeyCredentialRequestOptions.Builder setAllowList(List<PublicKeyCredentialDescriptor> var1) {
            this.allowList = var1;
            return this;
        }

        public final PublicKeyCredentialRequestOptions.Builder setAuthenticationExtensions(Map<String,Object> var1) {
            this.extensions = var1;
            return this;
        }

        public final PublicKeyCredentialRequestOptions.Builder setTimeoutSeconds(Double timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public final PublicKeyCredentialRequestOptions.Builder setTokenBinding(TokenBinding tokenBinding) {
            this.tokenBinding = tokenBinding;
            return this;
        }

        public final PublicKeyCredentialRequestOptions.Builder setOrigin(String origin) {
            this.origin = origin;
            return this;
        }

        public final PublicKeyCredentialRequestOptions build() {
            return new PublicKeyCredentialRequestOptions(this.challenge, this.rpId, this.allowList, this.extensions, this.timeoutSeconds, this.tokenBinding, this.origin);
        }
    }
}
