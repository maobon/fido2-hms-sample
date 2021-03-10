package com.gmrz.fido2.param.model;



public enum UserVerificationRequirement {

    REQUIRED("required"),PREFERRED("preferred"),DISCOURAGED("discouraged");

    private final String value;

    public static UserVerificationRequirement fromString(String var0) throws UnsupportedException {
        UserVerificationRequirement[] var1;
        int var2 = (var1 = values()).length;

        for(int var3 = 0; var3 < var2; ++var3) {
            UserVerificationRequirement var4 = var1[var3];
            if (var0.equals(var4.value)) {
                return var4;
            }
        }

        throw new UnsupportedException(var0);
    }

    UserVerificationRequirement(String var3) {
        this.value = var3;
    }

    public final String toString() {
        return this.value;
    }

    public static class UnsupportedException extends Exception {
        public UnsupportedException(String var1) {
            super(String.format("Attachment %s not supported", var1));
        }
    }
}


