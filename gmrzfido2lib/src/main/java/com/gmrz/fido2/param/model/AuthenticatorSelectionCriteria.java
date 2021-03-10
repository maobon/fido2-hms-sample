package com.gmrz.fido2.param.model;


public class AuthenticatorSelectionCriteria {

    private final Attachment authenticatorAttachment;

    private final Boolean requireResidentKey;

    private UserVerificationRequirement userVerification = UserVerificationRequirement.PREFERRED;

    public AuthenticatorSelectionCriteria(Attachment attachment, Boolean resident, UserVerificationRequirement userVerification){
        this.authenticatorAttachment = attachment;
        this.requireResidentKey = resident;
        this.userVerification = userVerification;
    }

    public Attachment getAuthenticatorAttachment(){
        return authenticatorAttachment;
    }

    public Boolean isRequireResidentKey(){
        return requireResidentKey;
    }

    public UserVerificationRequirement getUserVerification(){
        return userVerification;
    }

    public static class Builder{
        private Attachment attachment;
        private boolean requireResidentKey;
        private UserVerificationRequirement userVerification;

        public Builder() {
        }

        public final AuthenticatorSelectionCriteria.Builder setAttachment(Attachment attachment) {
            this.attachment = attachment;
            return this;
        }

        public final AuthenticatorSelectionCriteria.Builder setResidentKey(boolean resident) {
            this.requireResidentKey = resident;
            return this;
        }

        public final AuthenticatorSelectionCriteria.Builder setUserverification(UserVerificationRequirement userVerification) {
            this.userVerification = userVerification;
            return this;
        }

        public final AuthenticatorSelectionCriteria build() {
            return new AuthenticatorSelectionCriteria(this.attachment, this.requireResidentKey, this.userVerification);
        }
    }
}
