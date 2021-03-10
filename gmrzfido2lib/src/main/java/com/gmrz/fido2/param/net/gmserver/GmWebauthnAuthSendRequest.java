package com.gmrz.fido2.param.net.gmserver;

import com.gmrz.fido2.param.net.request.AssertionResultRequest;

public class GmWebauthnAuthSendRequest {

    public static class Context{
        public String transNo;
        public String userName;
        public String custNo;
        public String rf1;
        public String rf2;
        public String appID;
        public String[] authType;
        public String transType;
        public String protocol;
        public Device devices;
        public String rpId;
    }

    public Context context;

    public String serverData;

    public AssertionResultRequest credentials;

    public String userKey;

}