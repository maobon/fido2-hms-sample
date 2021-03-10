package com.gmrz.fido2.param.model;

public enum AuthenticatorTransport {
    USB("usb"), NFC("nfc"), BLE("ble");

    private final String name;

    /**
     * @param name
     */
    AuthenticatorTransport(String name) {
        this.name = name;
    }

    /**
     * @param s
     * @return Transport corresponding to the input string
     */
    public static AuthenticatorTransport decode(String s) {
        for (AuthenticatorTransport t : AuthenticatorTransport.values()) {
            if (t.name.equals(s)) {
                return t;
            }
        }
        throw new IllegalArgumentException(s + " not a valid Transport");
    }

    @Override
    public String toString() {
        return name;
    }
}
