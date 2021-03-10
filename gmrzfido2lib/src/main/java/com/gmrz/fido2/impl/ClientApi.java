package com.gmrz.fido2.impl;

import android.app.Activity;

//import com.gmrz.fido2.param.client.AuthenticatorAssertionResponse;
//import com.gmrz.fido2.param.client.AuthenticatorAttestationResponse;

import com.huawei.hms.support.api.fido.fido2.AuthenticatorAssertionResponse;
import com.huawei.hms.support.api.fido.fido2.AuthenticatorAttestationResponse;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialCreationOptions;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialRequestOptions;

public interface ClientApi {

    AuthenticatorAttestationResponse reg(Activity activity, PublicKeyCredentialCreationOptions options) throws ClientException;

    AuthenticatorAssertionResponse auth(Activity activity, PublicKeyCredentialRequestOptions options) throws ClientException;

    boolean isSupport(Activity activity);

}
