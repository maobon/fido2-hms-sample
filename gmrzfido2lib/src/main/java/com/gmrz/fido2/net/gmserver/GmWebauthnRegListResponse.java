package com.gmrz.fido2.net.gmserver;

import java.util.List;

public class GmWebauthnRegListResponse {

    public static class Authenticator{
        public String authType;
        public String transType;
        public String keyID;
        public String aaguid;
        public long createts;
    }

    public int statusCode;

    public String description;

    public List<Authenticator> authenticators;

}
