package com.gmrz.fido2.param.model;



public enum Attachment {
    PLATFORM("platform"),CROSS_PLATFORM("cross-platform");

    private final String value;

    public static Attachment fromString(String var0) throws Attachment.UnsupportedAttachmentException {
        Attachment[] var1;
        int var2 = (var1 = values()).length;

        for(int var3 = 0; var3 < var2; ++var3) {
            Attachment var4 = var1[var3];
            if (var0.equals(var4.value)) {
                return var4;
            }
        }

        throw new Attachment.UnsupportedAttachmentException(var0);
    }

    Attachment(String var3) {
        this.value = var3;
    }

    public final String toString() {
        return this.value;
    }

    public static class UnsupportedAttachmentException extends Exception {
        public UnsupportedAttachmentException(String var1) {
            super(String.format("Attachment %s not supported", var1));
        }
    }
}
