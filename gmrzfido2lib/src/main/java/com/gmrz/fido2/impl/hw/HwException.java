package com.gmrz.fido2.impl.hw;

public class HwException extends Exception {

    public static final int ERR_THREAD = 1;
    public static final int ERR_UNKNOWN_REQUEST = 2;
    public static final int ERR_UNKNOWN = 3;
    public static final int ERR_HW_START = 100;

    private final int status;

    private final CharSequence msg;

    public HwException(int status,CharSequence msg){
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public CharSequence getMsg() {
        return msg;
    }
}
