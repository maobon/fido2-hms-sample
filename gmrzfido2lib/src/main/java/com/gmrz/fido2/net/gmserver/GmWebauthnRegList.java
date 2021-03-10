package com.gmrz.fido2.net.gmserver;

public class GmWebauthnRegList {

    public static class Context{
        public String transNo;
        public String userName;
        public String appID;
        public String authType;
        public String transType;
        public String deviceID;
    }

    public Context context;
}