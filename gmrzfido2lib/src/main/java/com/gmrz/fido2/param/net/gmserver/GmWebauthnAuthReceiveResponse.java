package com.gmrz.fido2.param.net.gmserver;

import com.gmrz.fido2.param.net.idl.ServerPublicKeyCredentialCreationOptionsResponse;

public class GmWebauthnAuthReceiveResponse {

    public static class WebAuthnRequest{
        public ServerPublicKeyCredentialCreationOptionsResponse options;
        public String serverData;
    }

    public int statusCode;

    public String description;

    public WebAuthnRequest webAuthnRequest;

}
