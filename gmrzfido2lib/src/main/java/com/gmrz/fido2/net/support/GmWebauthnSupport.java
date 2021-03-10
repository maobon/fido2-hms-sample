package com.gmrz.fido2.net.support;

import com.gmrz.fido2.net.Device;

public class GmWebauthnSupport {

    public static class Context{
        public String transNo;
        public String[] authType;
        public String[] transType;
        public String appID;
        public Device devices;
    }

    public Context context;

}
