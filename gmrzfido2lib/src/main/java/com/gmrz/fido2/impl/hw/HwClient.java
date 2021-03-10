package com.gmrz.fido2.impl.hw;

import android.app.Activity;
import android.util.Log;

import com.gmrz.fido2.impl.ClientApi;
import com.gmrz.fido2.impl.ClientException;
import com.gmrz.fido2.param.client.AuthenticatorAssertionResponse;
import com.gmrz.fido2.param.client.AuthenticatorAttestationResponse;
import com.gmrz.fido2.utils.VerboseLogger;
import com.google.gson.Gson;
import com.huawei.hms.support.api.fido.fido2.Fido2;
import com.huawei.hms.support.api.fido.fido2.Fido2AuthenticationRequest;
import com.huawei.hms.support.api.fido.fido2.Fido2AuthenticationResponse;
import com.huawei.hms.support.api.fido.fido2.Fido2Client;
import com.huawei.hms.support.api.fido.fido2.Fido2RegistrationRequest;
import com.huawei.hms.support.api.fido.fido2.Fido2RegistrationResponse;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialCreationOptions;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialRequestOptions;

public class HwClient implements ClientApi {

    private static final String TAG = "HwClient";

    private final Gson gson = new Gson();

    // AuthenticatorAttestationResponse - gmrz
    // AuthenticatorAssertionResponse - gmrz

    @Override
    public AuthenticatorAttestationResponse reg(Activity activity, com.gmrz.fido2.param.client.PublicKeyCredentialCreationOptions options) throws ClientException {
        Log.d(TAG, "register start");
        PublicKeyCredentialCreationOptions hwRequestOptions = gson.fromJson(gson.toJson(options), PublicKeyCredentialCreationOptions.class);
        Log.d(TAG, "register options :" + gson.toJson(hwRequestOptions));
        Fido2RegistrationRequest hwRequest = new Fido2RegistrationRequest(hwRequestOptions, null);
        try {
            Fido2RegistrationResponse registrationResponse = HwIntentHelperActivity.reg(activity, hwRequest);
            if (registrationResponse.isSuccess()) {
                String responseData = gson.toJson(registrationResponse.getAuthenticatorAttestationResponse());
                // VerboseLogger.print(TAG, "huawei fido2 client register response:" + responseData);
                AuthenticatorAttestationResponse response = gson.fromJson(responseData, AuthenticatorAttestationResponse.class);
                return response;
            } else {
                Log.e(TAG, "err ctap2 status:" + registrationResponse.getCtapStatus() + " msg:" + registrationResponse.getCtapStatusMessage() + " fido2 status:" + registrationResponse.getFido2Status() + " fido2 msg:" + registrationResponse.getFido2StatusMessage());
                throw new ClientException(ClientException.ERR_CLIENT_START + registrationResponse.getFido2Status(), registrationResponse.getFido2StatusMessage());
            }
        } catch (HwException e) {
            Log.e(TAG, "hw exception status:" + e.getStatus() + " msg:" + e.getMsg());
            throw new ClientException(ClientException.ERR_TRANSPORT, e.getMsg());
        }
    }

    @Override
    public AuthenticatorAssertionResponse auth(Activity activity, com.gmrz.fido2.param.client.PublicKeyCredentialRequestOptions options) throws ClientException {
        Log.d(TAG, "auth start");
        PublicKeyCredentialRequestOptions hwRequestOptions = gson.fromJson(gson.toJson(options), PublicKeyCredentialRequestOptions.class);
        Fido2AuthenticationRequest hwRequest = new Fido2AuthenticationRequest(hwRequestOptions, null);
        try {
            Fido2AuthenticationResponse authenticationResponse = HwIntentHelperActivity.auth(activity, hwRequest);
            if (authenticationResponse.isSuccess()) {
                String responseData = gson.toJson(authenticationResponse.getAuthenticatorAssertionResponse());
                // VerboseLogger.print(TAG, "huawei fido2 client auth response:" + responseData);
                AuthenticatorAssertionResponse response = gson.fromJson(responseData, com.gmrz.fido2.param.client.AuthenticatorAssertionResponse.class);
                return response;
            } else {
                Log.e(TAG, "err ctap2 status:" + authenticationResponse.getCtapStatus() + " msg:" + authenticationResponse.getCtapStatusMessage() + " fido2 status:" + authenticationResponse.getFido2Status() + " fido2 msg:" + authenticationResponse.getFido2StatusMessage());
                throw new ClientException(ClientException.ERR_CLIENT_START + authenticationResponse.getFido2Status(), authenticationResponse.getFido2StatusMessage());
            }
        } catch (HwException e) {
            Log.e(TAG, "hw exception status:" + e.getStatus() + " msg:" + e.getMsg());
            throw new ClientException(ClientException.ERR_TRANSPORT, e.getMsg());
        }
    }

    @Override
    public boolean isSupport(Activity activity) {
        Fido2Client fido2Client = Fido2.getFido2Client(activity);
        return fido2Client.isSupported();
    }
}
