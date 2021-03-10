package com.gmrz.fido2.param.net.gmserver;

public class GmWebauthnRegReceiveRequest {

    public static class Context{
        public String transNo;
        public String userName;
        public String rf1;
        public String rf2;
        public String appID;
        public String transType;
        public String authType;
        public String protocol;
        public Device devices;
        public String rpId;
        public String authenticatorAttachment;
        public String attestation;
    }

    public Context context;

    public String accessToken;

}
