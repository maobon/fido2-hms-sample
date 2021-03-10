package com.gmrz.fido2.impl;

public class ClientException extends Exception {

    public static final int ERR_PARAM = 1;
    public static final int ERR_TRANSPORT = 2;
    public static final int ERR_CLIENT_START = 100;

    private final int status;

    private final CharSequence msg;

    public ClientException(int status,CharSequence msg){
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public CharSequence getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "ClientException{" +
                "status=" + status +
                ", msg=" + msg +
                '}';
    }
}
