package com.gmrz.fido2.param.client;

public class ClientStatus {

    public final static int OK = 0x0000;
    public final static int NOT_SUPPORTED = 0x0001;
    public final static int UNKNOWN = 0x0002;
    public final static int CANCEL = 0x0003;
    public final static int ATTESTATION_NOT_FIT = 0x0004;
    public final static int TIMEOUT = 0x0005;
    public final static int PARAM_ERR = 0x0006;

    public final static int AUTHENTICATOR_START = 0x0100;
    public final static int AUTHENTICATOR_END = 0x01FF;
}
