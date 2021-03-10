package com.gmrz.fido2.param.model;



public enum AttestationConveyancePreference {


    NONE("none"),INDIRECT("indirect"),DIRECT("direct");

    private final String value;

    public static AttestationConveyancePreference fromString(String var0) throws UnsupportedException {
        AttestationConveyancePreference[] var1;
        int var2 = (var1 = values()).length;

        for(int var3 = 0; var3 < var2; ++var3) {
            AttestationConveyancePreference var4 = var1[var3];
            if (var0.equals(var4.value)) {
                return var4;
            }
        }

        throw new UnsupportedException(var0);
    }

    AttestationConveyancePreference(String var3) {
        this.value = var3;
    }

    public final String toString() {
        return this.value;
    }

    public static class UnsupportedException extends Exception {
        public UnsupportedException(String var1) {
            super(String.format("AttestationConveyancePreference %s not supported", var1));
        }
    }

}
