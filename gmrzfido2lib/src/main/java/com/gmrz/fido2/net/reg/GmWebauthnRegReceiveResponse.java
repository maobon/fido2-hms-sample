package com.gmrz.fido2.net.reg;

import com.gmrz.fido2.net.idl.ServerPublicKeyCredentialCreationOptionsResponse;

public class GmWebauthnRegReceiveResponse {

    public static class WebAuthnRequest{
        public ServerPublicKeyCredentialCreationOptionsResponse options;
        public String serverData;
    }

    public int statusCode;

    public String description;

    public WebAuthnRequest webAuthnRequest;

}
