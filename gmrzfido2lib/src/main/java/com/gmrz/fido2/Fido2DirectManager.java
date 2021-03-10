package com.gmrz.fido2;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;

import com.gmrz.fido2.impl.ClientApi;
import com.gmrz.fido2.impl.ClientException;
import com.gmrz.fido2.impl.hw.HwClient;
import com.gmrz.fido2.param.client.AuthenticatorAssertionResponse;
import com.gmrz.fido2.param.client.AuthenticatorAttestationResponse;
import com.gmrz.fido2.param.client.ClientStatus;
import com.gmrz.fido2.param.client.PublicKeyCredentialCreationOptions;
import com.gmrz.fido2.param.client.PublicKeyCredentialRequestOptions;
import com.gmrz.fido2.param.model.Algorithm;
import com.gmrz.fido2.param.model.Attachment;
import com.gmrz.fido2.param.model.AuthenticatorSelectionCriteria;
import com.gmrz.fido2.param.model.AuthenticatorTransport;
import com.gmrz.fido2.param.model.PublicKeyCredentialDescriptor;
import com.gmrz.fido2.param.model.PublicKeyCredentialParameters;
import com.gmrz.fido2.param.model.PublicKeyCredentialRpEntity;
import com.gmrz.fido2.param.model.PublicKeyCredentialType;
import com.gmrz.fido2.param.model.PublicKeyCredentialUserEntity;
import com.gmrz.fido2.param.net.gmserver.Device;
import com.gmrz.fido2.param.net.gmserver.GmWebauthnAuthReceiveRequest;
import com.gmrz.fido2.param.net.gmserver.GmWebauthnAuthReceiveResponse;
import com.gmrz.fido2.param.net.gmserver.GmWebauthnAuthSendRequest;
import com.gmrz.fido2.param.net.gmserver.GmWebauthnAuthSendResponse;
import com.gmrz.fido2.param.net.gmserver.GmWebauthnDel;
import com.gmrz.fido2.param.net.gmserver.GmWebauthnDelResponse;
import com.gmrz.fido2.param.net.gmserver.GmWebauthnRegReceiveRequest;
import com.gmrz.fido2.param.net.gmserver.GmWebauthnRegReceiveResponse;
import com.gmrz.fido2.param.net.gmserver.GmWebauthnRegSendRequest;
import com.gmrz.fido2.param.net.gmserver.GmWebauthnRegSendResponse;
import com.gmrz.fido2.param.net.gmserver.GmWebauthnRegStatus;
import com.gmrz.fido2.param.net.gmserver.GmWebauthnRegStatusResponse;
import com.gmrz.fido2.param.net.idl.ServerPublicKeyCredentialDescriptor;
import com.gmrz.fido2.param.net.idl.ServerPublicKeyCredentialParameters;
import com.gmrz.fido2.param.net.idl.ServerPublicKeyCredentialCreationOptionsResponse;
import com.gmrz.fido2.param.net.request.AssertionResultRequest;
import com.gmrz.fido2.param.net.request.AssertionResultResponseRequest;
import com.gmrz.fido2.param.net.request.AttestationResultRequest;
import com.gmrz.fido2.param.net.request.AttestationResultResponseRequest;
import com.gmrz.fido2.utils.HttpDirectUtil;
import com.gmrz.fido2.utils.Logger;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Fido2DirectManager {

    private static final String TAG = "Fido2Manager";

    private static final String DEFAULT_DEVICEID = "default";

    private ClientApi clientApi;

    private final Gson gson = new Gson();

    // private static final String DEFAULT_URL = "http://192.168.6.140:8080";
    private static final String DEFAULT_URL = "http://fido.nationauth.cn:11114";

    // ......
    private static final String URL_REG_RECEIVE = "/uaf/reg/receive";
    private static final String URL_REG_SEND = "/uaf/reg/send";

    private static final String URL_AUTH_RECEIVE = "/uaf/auth/receive";
    private static final String URL_AUTH_SEND = "/uaf/auth/send";

    // ......
    private static final String URL_DELETE = "/uaf/webauthn/reg/delete";
    private static final String URL_REG_STATUS = "/uaf/reg/status";
    //

    private static final String META_WEBAUTHN_URL = "fido.webauthn.url";
    private static final String META_WEBAUTHN_KEY = "fido.webauthn.userkey";
    private static final String META_WEBAUTHN_RPID = "fido.webauthn.rpid";

    private Fido2DirectManager() {
    }

    private static volatile Fido2DirectManager instance = null;

    public static Fido2DirectManager getInstance() {
        if (instance == null) {
            synchronized (Fido2DirectManager.class) {
                if (instance == null) {
                    instance = new Fido2DirectManager();
                }
            }
        }
        return instance;
    }

    public Fido2DirectReInfo init(Activity activity) {
        Logger.d(TAG, "init start");
        Fido2DirectReInfo reInfo = new Fido2DirectReInfo();
        clientApi = initClientApi(activity);
        if (clientApi == null) {
            reInfo.status = Fido2DirectStatus.FAILED;
            return reInfo;
        }
        reInfo.status = Fido2DirectStatus.SUCCESS;
        return reInfo;
    }

    private ClientApi initClientApi(Activity activity) {
        HwClient hwClient = new HwClient();
        if (hwClient.isSupport(activity)) {
            return hwClient;
        }
        return null;
    }

    public Fido2DirectReInfo support(Activity activity) {
        Fido2DirectReInfo reInfo = new Fido2DirectReInfo();
        if (clientApi != null) {
            reInfo.status = Fido2DirectStatus.SUCCESS;
            return reInfo;
        }
        reInfo.status = Fido2DirectStatus.FAILED;
        return reInfo;
    }

    /**
     * 注册
     *
     * @param activity
     * @param userName
     * @param transNo
     * @param accessToken
     * @return
     */
    public Fido2DirectReInfo register(Activity activity, String userName, String transNo, String accessToken) {
        Logger.d(TAG, "register userName:" + userName + " transNo:" + transNo);
        Fido2DirectReInfo fidoReInfo = new Fido2DirectReInfo();
        fidoReInfo.status = Fido2DirectStatus.ERROR;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            fidoReInfo.status = Fido2DirectStatus.MAIN_THREAD_ERROR;
            return fidoReInfo;
        }
        if (clientApi == null) {
            // return with not support
            fidoReInfo.status = Fido2DirectStatus.NOT_SUPPORT;
            return fidoReInfo;
        }
        String serverData = null;

        // step1: -----------------
        // send request to server for first request
        GmWebauthnRegReceiveRequest receiveRequest = new GmWebauthnRegReceiveRequest();
        //receiveRequest.accessToken = accessToken;

        receiveRequest.context = new GmWebauthnRegReceiveRequest.Context();
        receiveRequest.context.protocol = "web";
        receiveRequest.context.authType = "30";
        receiveRequest.context.transNo = transNo;
        receiveRequest.context.transType = "00";
        receiveRequest.context.userName = userName;
        receiveRequest.context.devices = new Device();
        receiveRequest.context.devices.deviceID = DEFAULT_DEVICEID;
        receiveRequest.context.rpId = retrieveRpId(activity);
        receiveRequest.context.attestation = "none";
        receiveRequest.context.appID = "1103";

        // receiveRequest.context.authenticatorAttachment = "cross-platform";
        // receiveRequest.context.authenticatorAttachment = "platform";

        String receiveRequestStr = gson.toJson(receiveRequest);

        String url = retrieveUrl(activity) + URL_REG_RECEIVE;
        Logger.d(TAG, "Register First request: " + receiveRequestStr);
        String receiveResponseStr = HttpDirectUtil.doTlsPost(url, receiveRequestStr, null);
        Logger.d(TAG, "Register First response: " + receiveResponseStr);

        if (TextUtils.isEmpty(receiveResponseStr)) {
            Logger.e(TAG, "err!");
            // return exception
            fidoReInfo.status = Fido2DirectStatus.NETWORK_ERROR;
            return fidoReInfo;
        }

        if (!isServerMessageValid(receiveResponseStr)) {
            fidoReInfo.status = Fido2DirectStatus.SERVER_ERROR;
            return fidoReInfo;
        }

        GmWebauthnRegReceiveResponse receiveResponse = gson.fromJson(receiveResponseStr, GmWebauthnRegReceiveResponse.class);
        serverData = receiveResponse.webAuthnRequest.serverData;

        // step2: ---------------------
        // send request to client
        ServerPublicKeyCredentialCreationOptionsResponse webauthnRequest = receiveResponse.webAuthnRequest.options;
        //convert from server to fido2 api
        PublicKeyCredentialCreationOptions.Builder builder = new PublicKeyCredentialCreationOptions.Builder();

        // rp
        PublicKeyCredentialRpEntity rpEntity = new PublicKeyCredentialRpEntity();
        rpEntity.name = webauthnRequest.getRp().getName();
        rpEntity.id = webauthnRequest.getRp().getId();
        builder.setRp(rpEntity);

        // user
        PublicKeyCredentialUserEntity userEntity = new PublicKeyCredentialUserEntity();
        userEntity.id = webauthnRequest.getUser().getId().getBytes();
        userEntity.name = webauthnRequest.getUser().getId();
        userEntity.displayName = webauthnRequest.getUser().getDisplayName();
        builder.setUser(userEntity);

        // challenge
        builder.setChallenge(Base64.decode(webauthnRequest.getChallenge(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));

        // parameters
        if (webauthnRequest.getPubKeyCredParams() != null) {
            List<PublicKeyCredentialParameters> parameters = new ArrayList<>();
            ServerPublicKeyCredentialParameters[] serverParamsters = webauthnRequest.getPubKeyCredParams();
            for (ServerPublicKeyCredentialParameters serverParameter : serverParamsters) {
                PublicKeyCredentialParameters parameter = new PublicKeyCredentialParameters();
                parameter.alg = Algorithm.decode(serverParameter.getAlg());
                if (parameter.alg == Algorithm.ES256 || parameter.alg == Algorithm.PS256) {
                    parameter.type = PublicKeyCredentialType.PUBLIC_KEY;
                    parameters.add(parameter);
                }
            }
            builder.setParameters(parameters);
        }

        // excludeList
        if (webauthnRequest.getExcludeCredentials() != null) {
            List<PublicKeyCredentialDescriptor> parameters = new ArrayList<>();
            ServerPublicKeyCredentialDescriptor[] excludes = webauthnRequest.getExcludeCredentials();
            for (ServerPublicKeyCredentialDescriptor exclude : excludes) {
                PublicKeyCredentialDescriptor parameter = new PublicKeyCredentialDescriptor();
                parameter.id = Base64.decode(exclude.getId(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
                parameter.transports = new ArrayList<>();
                if (exclude.getTransports() != null) {
                    parameter.transports.add(AuthenticatorTransport.decode(exclude.getTransports()));
                }
                parameter.type = PublicKeyCredentialType.PUBLIC_KEY;
                parameters.add(parameter);
            }
            builder.setExcludeList(parameters);
        }

        // selection
        // Attachment attachment = Attachment.CROSS_PLATFORM;
        Attachment attachment = Attachment.PLATFORM;
        AuthenticatorSelectionCriteria selector = new AuthenticatorSelectionCriteria(attachment, null, null);
        builder.setAuthenticatorSelection(selector);

        // extensions
        if (webauthnRequest.getExtensions() != null) {
            builder.setAuthenticationExtensions(webauthnRequest.getExtensions());
        }

        PublicKeyCredentialCreationOptions clientRequest = builder.build();
        Logger.d(TAG, "client reg options:" + gson.toJson(clientRequest));
        AuthenticatorAttestationResponse clientResponse = null;

        try {
            clientResponse = clientApi.reg(activity, clientRequest);

        } catch (ClientException e) {
            e.printStackTrace();

            if (e.getStatus() > ClientException.ERR_CLIENT_START) {
                int clientRealStatus = e.getStatus() - ClientException.ERR_CLIENT_START;
                if (clientRealStatus == ClientStatus.CANCEL) {
                    fidoReInfo.status = Fido2DirectStatus.CANCEL;
                } else {
                    fidoReInfo.status = Fido2DirectStatus.FAILED;
                }
            } else {
                fidoReInfo.status = Fido2DirectStatus.FAILED;
            }
            return fidoReInfo;
        }

        // step3:
        // send request
        GmWebauthnRegSendRequest sendRequest = new GmWebauthnRegSendRequest();
        // sendRequest.accessToken = accessToken;
        sendRequest.context = new GmWebauthnRegSendRequest.Context();
        sendRequest.context.protocol = "web";
        sendRequest.context.authType = "30";
        sendRequest.context.transNo = transNo;
        sendRequest.context.transType = "00";
        sendRequest.context.userName = userName;
        sendRequest.context.devices = new Device();
        sendRequest.context.devices.deviceID = DEFAULT_DEVICEID;
        sendRequest.context.devices.deviceInfo = "TW96aWxsYS81LjAgKFdpbmRvd3MgTlQgMTAuMDsgV2luNjQ7IHg2NCkgQXBwbGVXZWJLaXQvNTM3LjM2IChLSFRNTCwgbGlrZSBHZWNrbykgQ2hyb21lLzcwLjAuMzUzOC4xMDIgU2FmYXJpLzUzNy4zNiBFZGdlLzE4LjE4MzYz";
        sendRequest.context.rpId = retrieveRpId(activity);
        sendRequest.context.custNo = userName;
        sendRequest.context.opType = "00";
        sendRequest.context.rpId = retrieveRpId(activity);
        sendRequest.context.appID = "1103";

        sendRequest.credentials = new AttestationResultRequest();
        AttestationResultResponseRequest attestationResponse = new AttestationResultResponseRequest();
        attestationResponse.setAttestationObject(Base64.encodeToString(clientResponse.getAttestationObject(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));
        attestationResponse.setClientDataJSON(Base64.encodeToString(clientResponse.getClientDataJson(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));
        sendRequest.credentials.setResponse(attestationResponse);

        sendRequest.credentials.setId(Base64.encodeToString(clientResponse.getCredentialId(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));
        sendRequest.credentials.setRawId(Base64.encodeToString(clientResponse.getCredentialId(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));
        sendRequest.credentials.setType("public-key");
        sendRequest.serverData = serverData;

        String sendRequestStr = gson.toJson(sendRequest);
        url = retrieveUrl(activity) + URL_REG_SEND;
        Logger.d(TAG, "Register Second request: " + sendRequestStr);
        String sendResponseStr = HttpDirectUtil.doTlsPost(url, sendRequestStr, null);
        Logger.d(TAG, "Register Second response: " + sendResponseStr);

        if (sendResponseStr == null) {
            fidoReInfo.status = Fido2DirectStatus.NETWORK_ERROR;
            return fidoReInfo;
        }

        GmWebauthnRegSendResponse sendResponse = gson.fromJson(sendResponseStr, GmWebauthnRegSendResponse.class);
        if (sendResponse.statusCode != 1200) {
            fidoReInfo.status = Fido2DirectStatus.SERVER_ERROR;
            return fidoReInfo;
        }

        fidoReInfo.status = Fido2DirectStatus.SUCCESS;
        fidoReInfo.token = sendResponse.token;
        return fidoReInfo;
    }

    /**
     * 认证
     *
     * @param activity
     * @param userName
     * @param transNo
     * @return
     */
    public Fido2DirectReInfo auth(Activity activity, String userName, String transNo) {
        Logger.d(TAG, "auth userName:" + userName + " transNo:" + transNo);
        Fido2DirectReInfo fidoReInfo = new Fido2DirectReInfo();
        fidoReInfo.status = Fido2DirectStatus.ERROR;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            fidoReInfo.status = Fido2DirectStatus.MAIN_THREAD_ERROR;
            return fidoReInfo;
        }
        if (clientApi == null) {
            // return with not support
            fidoReInfo.status = Fido2DirectStatus.NOT_SUPPORT;
            return fidoReInfo;
        }

        GmWebauthnAuthReceiveRequest receiveRequest = new GmWebauthnAuthReceiveRequest();
        receiveRequest.userKey = retrieveUserkey(activity);
        receiveRequest.context = new GmWebauthnAuthReceiveRequest.Context();
        receiveRequest.context.protocol = "web";
        receiveRequest.context.authType = new String[]{"30"};
        receiveRequest.context.transNo = transNo;
        receiveRequest.context.transType = "00";
        receiveRequest.context.userName = userName;
        receiveRequest.context.devices = new Device();
        receiveRequest.context.devices.deviceID = DEFAULT_DEVICEID;
        receiveRequest.context.rpId = retrieveRpId(activity);
        receiveRequest.context.appID = "1103";

        String receiveRequestStr = gson.toJson(receiveRequest);
        String url = retrieveUrl(activity) + URL_AUTH_RECEIVE;
        Logger.d(TAG, "Auth First request: " + receiveRequestStr);
        String receiveResponseStr = HttpDirectUtil.doTlsPost(url, receiveRequestStr, null);
        Logger.d(TAG, "Auth First response: " + receiveResponseStr);
        if (TextUtils.isEmpty(receiveResponseStr) || !isServerMessageValid(receiveResponseStr)) {
            Logger.e(TAG, "err!");
            //TODO return exception
            fidoReInfo.status = Fido2DirectStatus.SERVER_ERROR;
            return fidoReInfo;
        }
        GmWebauthnAuthReceiveResponse serverPublicKeyCredentialCreationOptionsResponse = gson.fromJson(receiveResponseStr, GmWebauthnAuthReceiveResponse.class);
        String serverData = serverPublicKeyCredentialCreationOptionsResponse.webAuthnRequest.serverData;
        ServerPublicKeyCredentialCreationOptionsResponse webauthnRequest = serverPublicKeyCredentialCreationOptionsResponse.webAuthnRequest.options;
        PublicKeyCredentialRequestOptions.Builder builder = new PublicKeyCredentialRequestOptions.Builder();
        //rpid
        builder.setRpId(webauthnRequest.getRpId());
        //challenge
        builder.setChallenge(Base64.decode(webauthnRequest.getChallenge(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));
        //allowlist
        if (webauthnRequest.getAllowCredentials() != null) {
            List<PublicKeyCredentialDescriptor> parameters = new ArrayList<>();
            ServerPublicKeyCredentialDescriptor[] allows = webauthnRequest.getAllowCredentials();
            for (ServerPublicKeyCredentialDescriptor allow : allows) {
                PublicKeyCredentialDescriptor parameter = new PublicKeyCredentialDescriptor();
                parameter.id = Base64.decode(allow.getId(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
                parameter.transports = new ArrayList<>();
                if (allow.getTransports() != null) {
                    parameter.transports.add(AuthenticatorTransport.decode(allow.getTransports()));
                }
                parameter.type = PublicKeyCredentialType.PUBLIC_KEY;
                parameters.add(parameter);
            }
            builder.setAllowList(parameters);
        }
        //extensions
        if (webauthnRequest.getExtensions() != null) {
            builder.setAuthenticationExtensions(webauthnRequest.getExtensions());
        }
        PublicKeyCredentialRequestOptions clientRequest = builder.build();
        Logger.d(TAG, "auth client request:" + gson.toJson(clientRequest));
        AuthenticatorAssertionResponse clientResponse = null;
        try {
            clientResponse = clientApi.auth(activity, clientRequest);
        } catch (ClientException e) {
            e.printStackTrace();
            // return with err
            if (e.getStatus() > ClientException.ERR_CLIENT_START) {
                int clientRealStatus = e.getStatus() - ClientException.ERR_CLIENT_START;
                if (clientRealStatus == ClientStatus.CANCEL) {
                    fidoReInfo.status = Fido2DirectStatus.CANCEL;
                } else {
                    fidoReInfo.status = Fido2DirectStatus.FAILED;
                }
            } else {
                fidoReInfo.status = Fido2DirectStatus.FAILED;
            }
            return fidoReInfo;
        }
        Logger.d(TAG, "auth client response:" + gson.toJson(clientResponse));
        GmWebauthnAuthSendRequest request = new GmWebauthnAuthSendRequest();
        request.userKey = retrieveUserkey(activity);
        request.context = new GmWebauthnAuthSendRequest.Context();
        request.context.protocol = "web";
        request.context.authType = new String[]{"30"};
        request.context.transNo = transNo;
        request.context.transType = "00";
        request.context.userName = userName;
        request.context.custNo = userName;
        request.context.devices = new Device();
        request.context.devices.deviceID = DEFAULT_DEVICEID;
        request.context.devices.deviceInfo = "TW96aWxsYS81LjAgKFdpbmRvd3MgTlQgMTAuMDsgV2luNjQ7IHg2NCkgQXBwbGVXZWJLaXQvNTM3LjM2IChLSFRNTCwgbGlrZSBHZWNrbykgQ2hyb21lLzcwLjAuMzUzOC4xMDIgU2FmYXJpLzUzNy4zNiBFZGdlLzE4LjE4MzYz";
        request.context.rpId = retrieveRpId(activity);
        request.context.appID = "1103";

        AssertionResultRequest webauthnequest = new AssertionResultRequest();
        AssertionResultResponseRequest assertionResultResponse = new AssertionResultResponseRequest();
        assertionResultResponse.setSignature(Base64.encodeToString(clientResponse.getSignature(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));
        assertionResultResponse.setClientDataJSON(Base64.encodeToString(clientResponse.getClientDataJson(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));
        assertionResultResponse.setAuthenticatorData(Base64.encodeToString(clientResponse.getAuthenticatorData(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));
        webauthnequest.setResponse(assertionResultResponse);
        webauthnequest.setId(Base64.encodeToString(clientResponse.getCredentialId(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));
        webauthnequest.setRawId(Base64.encodeToString(clientResponse.getCredentialId(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));
        webauthnequest.setType("public-key");
        request.credentials = webauthnequest;
        request.serverData = serverData;
        String sendRequestStr = gson.toJson(request);
        url = retrieveUrl(activity) + URL_AUTH_SEND;
        Logger.d(TAG, "Auth Second request: " + sendRequestStr);
        String sendResponseStr = HttpDirectUtil.doTlsPost(url, sendRequestStr, null);
        Logger.d(TAG, "Auth Second response: " + sendResponseStr);
        if (TextUtils.isEmpty(sendResponseStr)) {
            Logger.e(TAG, "err!");
            fidoReInfo.status = Fido2DirectStatus.NETWORK_ERROR;
            return fidoReInfo;
        }
        GmWebauthnAuthSendResponse sendResponse = gson.fromJson(sendResponseStr, GmWebauthnAuthSendResponse.class);
        if (sendResponse.statusCode != 1200) {
            fidoReInfo.status = Fido2DirectStatus.SERVER_ERROR;
            return fidoReInfo;
        }
        fidoReInfo.status = Fido2DirectStatus.SUCCESS;
        fidoReInfo.token = sendResponse.token;
        return fidoReInfo;
    }

    /**
     * 注销
     *
     * @param activity
     * @param userName
     * @param transNo
     * @param credentialId
     * @param accessToken
     * @return
     */
    public Fido2DirectReInfo dereg(Activity activity, String userName, String transNo, String credentialId, String accessToken) {
        Logger.d(TAG, "dereg userName:" + userName + " transNo:" + transNo + " credentialId:" + credentialId);
        Fido2DirectReInfo fidoReInfo = new Fido2DirectReInfo();
        fidoReInfo.status = Fido2DirectStatus.ERROR;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            fidoReInfo.status = Fido2DirectStatus.MAIN_THREAD_ERROR;
            return fidoReInfo;
        }
        GmWebauthnDel request = new GmWebauthnDel();
        request.accessToken = accessToken;
        request.context = new GmWebauthnDel.Context();
        request.context.userName = userName;
        request.context.transNo = transNo;
        request.context.authType = "30";
        request.context.transType = "00";
        request.context.protocol = "web";
        request.context.appID = "1103";

        request.context.rpId = retrieveRpId(activity);
        if (!TextUtils.isEmpty(credentialId)) {
            request.context.keyID = credentialId;
        }
        request.context.deviceID = DEFAULT_DEVICEID;
        String requestStr = gson.toJson(request);
        String url = retrieveUrl(activity) + URL_DELETE;
        Logger.d(TAG, "Delete request: " + requestStr);
        String sendResponseStr = HttpDirectUtil.doTlsPost(url, requestStr, null);
        Logger.d(TAG, "Delete response: " + sendResponseStr);
        if (TextUtils.isEmpty(sendResponseStr)) {
            Logger.e(TAG, "err!");
            fidoReInfo.status = Fido2DirectStatus.NETWORK_ERROR;
            return fidoReInfo;
        }
        GmWebauthnDelResponse sendResponse = gson.fromJson(sendResponseStr, GmWebauthnDelResponse.class);
        if (sendResponse.statusCode != 1200) {
            fidoReInfo.status = Fido2DirectStatus.SERVER_ERROR;
            return fidoReInfo;
        }
        fidoReInfo.status = Fido2DirectStatus.SUCCESS;
        fidoReInfo.token = sendResponse.token;
        return fidoReInfo;
    }

    public Fido2DirectReInfo status(Activity activity, String userName, String transNo) {
        Logger.d(TAG, "status userName:" + userName + " transNo:" + transNo);
        Fido2DirectReInfo fidoReInfo = new Fido2DirectReInfo();
        fidoReInfo.status = Fido2DirectStatus.ERROR;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            fidoReInfo.status = Fido2DirectStatus.MAIN_THREAD_ERROR;
            return fidoReInfo;
        }

        GmWebauthnRegStatus request = new GmWebauthnRegStatus();
        request.userKey = retrieveUserkey(activity);
        request.context = new GmWebauthnRegStatus.Context();
        request.context.authType = new String[]{"30"};
        request.context.transNo = transNo;
        request.context.transType = new String[]{"00"};
        request.context.userName = userName;
        request.context.devices = new Device();
        request.context.devices.deviceID = DEFAULT_DEVICEID;
        request.context.rpId = retrieveRpId(activity);
        String requestStr = gson.toJson(request);
        String url = retrieveUrl(activity) + URL_REG_STATUS;
        Logger.d(TAG, "Status request: " + requestStr);
        String sendResponseStr = HttpDirectUtil.doTlsPost(url, requestStr, null);
        Logger.d(TAG, "Status response: " + sendResponseStr);
        if (TextUtils.isEmpty(sendResponseStr) || !isServerMessageValid(sendResponseStr)) {
            Logger.e(TAG, "err!");
            // return exception
            return fidoReInfo;
        }
        GmWebauthnRegStatusResponse response = gson.fromJson(sendResponseStr, GmWebauthnRegStatusResponse.class);
        if (response.statusCode != 1200) {
            fidoReInfo.status = Fido2DirectStatus.SERVER_ERROR;
            return fidoReInfo;
        }
        fidoReInfo.status = Fido2DirectStatus.SUCCESS;
        if (response.regStatus != null) {
            if ("1".equals(response.regStatus[0][0])) {
                fidoReInfo.reged = true;
            }
        }
        return fidoReInfo;
    }

    private String url = null;
    private String userKey = null;
    private String rpId = null;

    public void initAppInfo(String url, String userKey, String rpId) {
        this.url = url;
        this.userKey = userKey;
        this.rpId = rpId;
    }

    private String retrieveUserkey(Context ctx) {
        if (!TextUtils.isEmpty(userKey)) {
            return userKey;
        }
        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = info.applicationInfo.metaData;
            String appkey = bundle.getString(META_WEBAUTHN_KEY);
            Logger.d(TAG, "userkey:" + appkey);
            return appkey;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String retrieveUrl(Context ctx) {
        if (!TextUtils.isEmpty(url)) {
            return url;
        }
        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = info.applicationInfo.metaData;
            String appkey = bundle.getString(META_WEBAUTHN_URL);
            if (TextUtils.isEmpty(appkey)) {
                appkey = DEFAULT_URL;
            }
            return appkey;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String retrieveRpId(Context ctx) {
        if (!TextUtils.isEmpty(rpId)) {
            return rpId;
        }
        try {
            PackageInfo info = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = info.applicationInfo.metaData;
            String appkey = bundle.getString(META_WEBAUTHN_RPID);
            return appkey;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isServerMessageValid(String serverMessage) {
        boolean retval = false;
        try {
            int statusCode = ((Integer) new JSONObject(serverMessage).get("statusCode")).intValue();
            retval = statusCode == 1200;
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
        return retval;
    }
}
