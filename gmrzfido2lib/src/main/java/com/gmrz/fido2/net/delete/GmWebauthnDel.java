package com.gmrz.fido2.net.delete;

public class GmWebauthnDel {

    public static class Context{
        public String transNo;
        public String userName;
        public String appID;
        public String keyID;
        public String deviceID;
        public String authType;
        public String protocol;
        public String transType;
        public String rpId;
    }

    public Context context;

    public String accessToken;

}
