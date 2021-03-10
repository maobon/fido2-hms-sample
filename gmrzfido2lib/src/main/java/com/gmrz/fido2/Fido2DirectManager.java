package com.gmrz.fido2;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.gmrz.fido2.impl.ClientApi;
import com.gmrz.fido2.impl.ClientException;
import com.gmrz.fido2.impl.hw.HwClient;
import com.gmrz.fido2.net.Device;
import com.gmrz.fido2.net.auth.GmWebauthnAuthReceiveRequest;
import com.gmrz.fido2.net.auth.GmWebauthnAuthReceiveResponse;
import com.gmrz.fido2.net.auth.GmWebauthnAuthSendRequest;
import com.gmrz.fido2.net.auth.GmWebauthnAuthSendResponse;
import com.gmrz.fido2.net.delete.GmWebauthnDel;
import com.gmrz.fido2.net.delete.GmWebauthnDelResponse;
import com.gmrz.fido2.net.gmserver.GmWebauthnRegStatus;
import com.gmrz.fido2.net.gmserver.GmWebauthnRegStatusResponse;
import com.gmrz.fido2.net.idl.ServerPublicKeyCredentialCreationOptionsResponse;
import com.gmrz.fido2.net.idl.ServerPublicKeyCredentialDescriptor;
import com.gmrz.fido2.net.idl.ServerPublicKeyCredentialParameters;
import com.gmrz.fido2.net.reg.GmWebauthnRegReceiveRequest;
import com.gmrz.fido2.net.reg.GmWebauthnRegReceiveResponse;
import com.gmrz.fido2.net.reg.GmWebauthnRegSendRequest;
import com.gmrz.fido2.net.reg.GmWebauthnRegSendResponse;
import com.gmrz.fido2.net.request.AssertionResultRequest;
import com.gmrz.fido2.net.request.AssertionResultResponseRequest;
import com.gmrz.fido2.net.request.AttestationResultRequest;
import com.gmrz.fido2.net.request.AttestationResultResponseRequest;
import com.gmrz.fido2.utils.ByteUtils;
import com.gmrz.fido2.utils.HttpDirectUtil;
import com.gmrz.fido2.utils.Logger;
import com.google.gson.Gson;
import com.huawei.hms.support.api.fido.fido2.Algorithm;
import com.huawei.hms.support.api.fido.fido2.Attachment;
import com.huawei.hms.support.api.fido.fido2.AuthenticatorAssertionResponse;
import com.huawei.hms.support.api.fido.fido2.AuthenticatorAttestationResponse;
import com.huawei.hms.support.api.fido.fido2.AuthenticatorSelectionCriteria;
import com.huawei.hms.support.api.fido.fido2.AuthenticatorTransport;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialCreationOptions;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialDescriptor;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialParameters;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialRequestOptions;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialRpEntity;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialType;
import com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialUserEntity;
import com.huawei.hms.support.api.fido.fido2.UserVerificationRequirement;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
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
     * @return
     */
    public Fido2DirectReInfo register(Activity activity, String userName, String transNo) {
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

        receiveRequest.context = new GmWebauthnRegReceiveRequest.Context();
        receiveRequest.context.appID = "1103";
        receiveRequest.context.protocol = "web";
        receiveRequest.context.authType = "30";
        receiveRequest.context.transNo = transNo;
        receiveRequest.context.transType = "00";
        receiveRequest.context.userName = userName;

        receiveRequest.context.devices = new Device();
        receiveRequest.context.devices.deviceID = DEFAULT_DEVICEID;

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
        // convert from server to fido2 api
        PublicKeyCredentialCreationOptions.Builder builder = new PublicKeyCredentialCreationOptions.Builder();

        // rp
        String rpId = webauthnRequest.getRp().getId();
        String rpName = webauthnRequest.getRp().getName();

        PublicKeyCredentialRpEntity rpEntity = new PublicKeyCredentialRpEntity(rpId, rpName, rpName);
        builder.setRp(rpEntity);

        // user
        // PublicKeyCredentialUserEntity userEntity = new PublicKeyCredentialUserEntity();
        // userEntity.id = webauthnRequest.getUser().getId().getBytes();
        // userEntity.name = webauthnRequest.getUser().getId();
        // userEntity.displayName = webauthnRequest.getUser().getDisplayName();
        // builder.setUser(userEntity);

        // user

        builder.setUser(new PublicKeyCredentialUserEntity(rpId, rpId.getBytes(StandardCharsets.UTF_8)));


        // challenge
        builder.setChallenge(Base64.decode(webauthnRequest.getChallenge(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));

        // parameters
        // if (webauthnRequest.getPubKeyCredParams() != null) {
        //     List<PublicKeyCredentialParameters> parameters = new ArrayList<>();
        //     ServerPublicKeyCredentialParameters[] serverParamsters = webauthnRequest.getPubKeyCredParams();
        //     for (ServerPublicKeyCredentialParameters serverParameter : serverParamsters) {
        //         PublicKeyCredentialParameters parameter = new PublicKeyCredentialParameters();
        //         parameter.alg = Algorithm.decode(serverParameter.getAlg());
        //         if (parameter.alg == Algorithm.ES256 || parameter.alg == Algorithm.PS256) {
        //             parameter.type = PublicKeyCredentialType.PUBLIC_KEY;
        //             parameters.add(parameter);
        //         }
        //     }
        //     builder.setParameters(parameters);
        // }

        // parameters
        if (webauthnRequest.getPubKeyCredParams() != null) {
            List<PublicKeyCredentialParameters> parameters = new ArrayList<>();
            ServerPublicKeyCredentialParameters[] serverPublicKeyCredentialParameters = webauthnRequest.getPubKeyCredParams();
            for (ServerPublicKeyCredentialParameters param : serverPublicKeyCredentialParameters) {
                try {
                    PublicKeyCredentialParameters parameter = new PublicKeyCredentialParameters(PublicKeyCredentialType.PUBLIC_KEY, Algorithm.fromCode(param.getAlg()));
                    parameters.add(parameter);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
            builder.setPubKeyCredParams(parameters);
        }

        // excludeList
        // if (webauthnRequest.getExcludeCredentials() != null) {
        //     List<PublicKeyCredentialDescriptor> parameters = new ArrayList<>();
        //     ServerPublicKeyCredentialDescriptor[] excludes = webauthnRequest.getExcludeCredentials();
        //     for (ServerPublicKeyCredentialDescriptor exclude : excludes) {
        //         PublicKeyCredentialDescriptor parameter = new PublicKeyCredentialDescriptor();
        //         parameter.id = Base64.decode(exclude.getId(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
        //         parameter.transports = new ArrayList<>();
        //         if (exclude.getTransports() != null) {
        //             parameter.transports.add(AuthenticatorTransport.decode(exclude.getTransports()));
        //         }
        //         parameter.type = PublicKeyCredentialType.PUBLIC_KEY;
        //         parameters.add(parameter);
        //     }
        //     builder.setExcludeList(parameters);
        // }

        if (webauthnRequest.getExcludeCredentials() != null) {
            List<com.huawei.hms.support.api.fido.fido2.PublicKeyCredentialDescriptor> descriptors = new ArrayList<>();
            ServerPublicKeyCredentialDescriptor[] serverDescriptors = webauthnRequest.getExcludeCredentials();
            for (ServerPublicKeyCredentialDescriptor desc : serverDescriptors) {
                ArrayList<AuthenticatorTransport> transports = new ArrayList<>();
                if (desc.getTransports() != null) {
                    try {
                        transports.add(AuthenticatorTransport.fromValue(desc.getTransports()));
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }

                PublicKeyCredentialDescriptor descriptor = new PublicKeyCredentialDescriptor(PublicKeyCredentialType.PUBLIC_KEY, ByteUtils.base642Byte(desc.getId()), transports);
                descriptors.add(descriptor);
            }
            builder.setExcludeList(descriptors);
        }

        // selection
        // Attachment attachment = Attachment.CROSS_PLATFORM;
        // Attachment attachment = Attachment.PLATFORM;
        // AuthenticatorSelectionCriteria selector = new AuthenticatorSelectionCriteria(attachment, null, null);
        // builder.setAuthenticatorSelection(selector);

        Attachment attachment = null;
        attachment = Attachment.PLATFORM; // TODO ..... 下面解析赋值失败了 需要检查为什么 ....

        if (webauthnRequest.getAuthenticatorSelection() != null) {

            AuthenticatorSelectionCriteria selectionCriteria = webauthnRequest.getAuthenticatorSelection();
            if (selectionCriteria.getAuthenticatorAttachment() != null) {
                try {
                    attachment = selectionCriteria.getAuthenticatorAttachment();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            Boolean residentKey = selectionCriteria.isRequireResidentKey();

            UserVerificationRequirement requirement = null;
            if (selectionCriteria.getUserVerification() != null) {
                try {
                    requirement = selectionCriteria.getUserVerification();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

            AuthenticatorSelectionCriteria fido2Selection = new AuthenticatorSelectionCriteria(attachment, residentKey, requirement);
            builder.setAuthenticatorSelection(fido2Selection);
        }

        // extensions
        if (webauthnRequest.getExtensions() != null) {
            // builder.setAuthenticationExtensions(webauthnRequest.getExtensions());
            builder.setExtensions(webauthnRequest.getExtensions());
        }

        // timeout
        builder.setTimeoutSeconds(webauthnRequest.getTimeout());

        PublicKeyCredentialCreationOptions clientRequest = builder.build();
        Logger.d(TAG, "client reg options:" + gson.toJson(clientRequest));

        AuthenticatorAttestationResponse clientResponse;

        try {
            clientResponse = clientApi.reg(activity, clientRequest);

        } catch (ClientException e) {
            e.printStackTrace();

            if (e.getStatus() > ClientException.ERR_CLIENT_START) {
                int clientRealStatus = e.getStatus() - ClientException.ERR_CLIENT_START;
                // ClientStatus.CANCEL = 0x0003;
                if (clientRealStatus == 0x0003) {
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
        receiveRequest.context = new GmWebauthnAuthReceiveRequest.Context();
        receiveRequest.context.protocol = "web";
        receiveRequest.context.authType = new String[]{"30"};
        receiveRequest.context.transNo = transNo;
        receiveRequest.context.transType = "00";
        receiveRequest.context.userName = userName;
        receiveRequest.context.devices = new Device();
        receiveRequest.context.devices.deviceID = DEFAULT_DEVICEID;
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

        GmWebauthnAuthReceiveResponse gmWebauthnAuthReceiveResponse = gson.fromJson(receiveResponseStr, GmWebauthnAuthReceiveResponse.class);
        String serverData = gmWebauthnAuthReceiveResponse.webAuthnRequest.serverData;

        ServerPublicKeyCredentialCreationOptionsResponse webauthnRequest = gmWebauthnAuthReceiveResponse.webAuthnRequest.options;

        // TODO .... 更换华为SDK的类
        PublicKeyCredentialRequestOptions.Builder builder = new PublicKeyCredentialRequestOptions.Builder();

        // rpid
        builder.setRpId(webauthnRequest.getRpId());

        // challenge
        builder.setChallenge(Base64.decode(webauthnRequest.getChallenge(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP));

        // allow list
        // if (webauthnRequest.getAllowCredentials() != null) {
        //     List<PublicKeyCredentialDescriptor> parameters = new ArrayList<>();
        //     ServerPublicKeyCredentialDescriptor[] allows = webauthnRequest.getAllowCredentials();
        //     for (ServerPublicKeyCredentialDescriptor allow : allows) {
        //         PublicKeyCredentialDescriptor parameter = new PublicKeyCredentialDescriptor();
        //         parameter.id = Base64.decode(allow.getId(), Base64.URL_SAFE | Base64.NO_PADDING | Base64.NO_WRAP);
        //         parameter.transports = new ArrayList<>();
        //         if (allow.getTransports() != null) {
        //             parameter.transports.add(AuthenticatorTransport.decode(allow.getTransports()));
        //         }
        //         parameter.type = PublicKeyCredentialType.PUBLIC_KEY;
        //         parameters.add(parameter);
        //     }
        //     builder.setAllowList(parameters);
        // }

        ServerPublicKeyCredentialDescriptor[] descriptors = webauthnRequest.getAllowCredentials();
        if (descriptors != null) {
            List<PublicKeyCredentialDescriptor> descriptorList = new ArrayList<>();
            for (ServerPublicKeyCredentialDescriptor descriptor : descriptors) {
                ArrayList<AuthenticatorTransport> transports = new ArrayList<>();
                if (descriptor.getTransports() != null) {
                    try {
                        transports.add(AuthenticatorTransport.fromValue(descriptor.getTransports()));
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
                PublicKeyCredentialDescriptor desc = new PublicKeyCredentialDescriptor(PublicKeyCredentialType.PUBLIC_KEY, ByteUtils.base642Byte(descriptor.getId()), transports);
                descriptorList.add(desc);
            }
            builder.setAllowList(descriptorList);
        }

        // extensions
        HashMap<String, Object> extensions = new HashMap<>();
        if (webauthnRequest.getExtensions() != null) {
            // builder.setAuthenticationExtensions(webauthnRequest.getExtensions());
            extensions.putAll(webauthnRequest.getExtensions());
        }

        builder.setExtensions(extensions);

        // timeout
        builder.setTimeoutSeconds(webauthnRequest.getTimeout());

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
                // ClientStatus.CANCEL = 0x0003;
                if (clientRealStatus == 0x0003) {
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
        // request.userKey = retrieveUserkey(activity);
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
     * @return
     */
    public Fido2DirectReInfo dereg(Activity activity, String userName, String transNo, String credentialId) {
        Logger.d(TAG, "dereg userName:" + userName + " transNo:" + transNo + " credentialId:" + credentialId);
        Fido2DirectReInfo fidoReInfo = new Fido2DirectReInfo();
        fidoReInfo.status = Fido2DirectStatus.ERROR;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            fidoReInfo.status = Fido2DirectStatus.MAIN_THREAD_ERROR;
            return fidoReInfo;
        }

        GmWebauthnDel request = new GmWebauthnDel();
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
        // request.userKey = retrieveUserkey(activity);
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
    private String rpId = null;

    public void initAppInfo(String url, String rpId) {
        this.url = url;
        this.rpId = rpId;
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
            int statusCode = (Integer) new JSONObject(serverMessage).get("statusCode");
            retval = statusCode == 1200;
        } catch (JSONException e) {
            Logger.e(TAG, e.getMessage());
        }
        return retval;
    }
}
