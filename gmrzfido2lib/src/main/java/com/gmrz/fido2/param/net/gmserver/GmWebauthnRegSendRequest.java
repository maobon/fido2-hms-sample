package com.gmrz.fido2.param.net.gmserver;

import com.gmrz.fido2.param.net.request.AttestationResultRequest;

public class GmWebauthnRegSendRequest {

    public static class Context{
        public String transNo;
        public String userName;
        public String custNo;
        public String rf1;
        public String rf2;
        public String appID;
        public String transType;
        public String authType;
        public String protocol;
        public Device devices;
        public String rpId;
        public String opType;
    }

    public AttestationResultRequest credentials;

    public String serverData;

    public Context context;

    public String accessToken;

}
