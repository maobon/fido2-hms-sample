package com.gmrz.fido2.net.gmserver;

import com.gmrz.fido2.net.Device;

public class GmWebauthnRegStatus {

    public static class Context{
        public String transNo;
        public String userName;
        public String rf1;
        public String rf2;
        public String appID;
        public String[] transType;
        public String[] authType;
        public Device devices;
        public String rpId;
    }

    public Context context;

    public String userKey;

}
