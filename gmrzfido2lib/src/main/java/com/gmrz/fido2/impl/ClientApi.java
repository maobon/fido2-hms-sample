package com.gmrz.fido2.impl;

import android.app.Activity;

import com.gmrz.fido2.param.client.AuthenticatorAssertionResponse;
import com.gmrz.fido2.param.client.AuthenticatorAttestationResponse;
import com.gmrz.fido2.param.client.PublicKeyCredentialCreationOptions;
import com.gmrz.fido2.param.client.PublicKeyCredentialRequestOptions;

public interface ClientApi {

    AuthenticatorAttestationResponse reg(Activity activity, PublicKeyCredentialCreationOptions options) throws ClientException;

    AuthenticatorAssertionResponse auth(Activity activity, PublicKeyCredentialRequestOptions options) throws ClientException;

    boolean isSupport(Activity activity);

}
