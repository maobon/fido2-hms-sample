package com.gmrz.fido2.impl.hw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;

import com.huawei.hms.support.api.fido.fido2.Fido2;
import com.huawei.hms.support.api.fido.fido2.Fido2AuthenticationRequest;
import com.huawei.hms.support.api.fido.fido2.Fido2AuthenticationResponse;
import com.huawei.hms.support.api.fido.fido2.Fido2Client;
import com.huawei.hms.support.api.fido.fido2.Fido2Intent;
import com.huawei.hms.support.api.fido.fido2.Fido2IntentCallback;
import com.huawei.hms.support.api.fido.fido2.Fido2RegistrationRequest;
import com.huawei.hms.support.api.fido.fido2.Fido2RegistrationResponse;
import com.huawei.hms.support.api.fido.fido2.NativeFido2AuthenticationOptions;
import com.huawei.hms.support.api.fido.fido2.NativeFido2Options;
import com.huawei.hms.support.api.fido.fido2.NativeFido2RegistrationOptions;

public class HwIntentHelperActivity extends Activity {

    private static final String TAG = "HwIntentHelperActivity";

    private static final String EXTRA_REQUEST_TYPE = "type";

    private static Fido2RegistrationRequest regRequest;
    private static Fido2RegistrationResponse regResponse;
    private static Fido2AuthenticationRequest authRequest;
    private static Fido2AuthenticationResponse authResponse;
    private static HwException hwException;

    private static final Object semaphore = new Object();

    private static void lock() {
        synchronized (semaphore) {
            try {
                semaphore.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void unlock() {
        synchronized (semaphore) {
            semaphore.notify();
        }
    }

    public static synchronized Fido2RegistrationResponse reg(Activity activity, Fido2RegistrationRequest request) throws HwException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Log.e(TAG, "can't run in main thread!");
            throw new HwException(HwException.ERR_THREAD, null);
        }
        Intent it = new Intent();
        it.setClass(activity, HwIntentHelperActivity.class);
        HwIntentHelperActivity.regRequest = request;
        HwIntentHelperActivity.regResponse = null;
        HwIntentHelperActivity.hwException = null;
        it.putExtra(EXTRA_REQUEST_TYPE, "reg");
        activity.startActivity(it);
        lock();
        if (hwException != null) {
            throw hwException;
        }
        return regResponse;
    }

    public static synchronized Fido2AuthenticationResponse auth(Activity activity, Fido2AuthenticationRequest request) throws HwException {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Log.e(TAG, "can't run in main thread!");
            throw new HwException(HwException.ERR_THREAD, null);
        }
        Intent it = new Intent();
        it.setClass(activity, HwIntentHelperActivity.class);
        HwIntentHelperActivity.authRequest = request;
        it.putExtra(EXTRA_REQUEST_TYPE, "auth");
        activity.startActivity(it);
        lock();
        if (hwException != null) {
            throw hwException;
        }
        return authResponse;
    }


    private Fido2Client fido2Client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "IntentHelperActivity onCreate");

        fido2Client = Fido2.getFido2Client(this);

        String extraRegType = getIntent().getStringExtra(EXTRA_REQUEST_TYPE);
        if ("reg".equals(extraRegType)) {
            // -------- 注册 -----------

            // fido2Client.getRegistrationIntent(p1, p2, p3);
            // TODO p2: NativeFido2RegistrationOptions.DEFAULT_OPTIONS .... 控制指纹窗口显示内容

            // 刷指纹窗口 显示内容配置
            NativeFido2RegistrationOptions.Builder builder = NativeFido2RegistrationOptions.builder();
            NativeFido2Options.BiometricPromptInfo biometricPromptInfo = new NativeFido2Options.BiometricPromptInfo("国民认证 FIDO2", "注册:验证您的指纹");
            builder.setBiometricPromptInfo(biometricPromptInfo);
            NativeFido2RegistrationOptions registrationOptions = builder.build();
            //

            fido2Client.getRegistrationIntent(regRequest, registrationOptions,
                    new Fido2IntentCallback() {
                        @Override
                        public void onSuccess(Fido2Intent fido2Intent) {
                            // 通过Fido2Client.REGISTRATION_REQUEST，启动FIDO客户端注册流程。
                            fido2Intent.launchFido2Activity(HwIntentHelperActivity.this, Fido2Client.REGISTRATION_REQUEST);
                        }

                        @Override
                        public void onFailure(int errorCode, CharSequence errString) {
                            Log.d(TAG, "reg intent get failed, errorCode:" + errorCode + " errString:" + errString);
                            // return err
                            hwException = new HwException(HwException.ERR_HW_START + errorCode, errString);
                            unlock();
                        }
                    });

        } else if ("auth".equals(extraRegType)) {
            // -------- 认证 -----------

            // fido2Client.getAuthenticationIntent(p1, p2, p3);
            // TODO p2: NativeFido2RegistrationOptions.DEFAULT_OPTIONS .... 控制指纹窗口显示内容

            // 刷指纹窗口 显示内容配置
            NativeFido2AuthenticationOptions.Builder builder = NativeFido2AuthenticationOptions.builder();
            NativeFido2Options.BiometricPromptInfo biometricPromptInfo = new NativeFido2Options.BiometricPromptInfo("国民认证 FIDO2", "认证:验证您的指纹");
            builder.setBiometricPromptInfo(biometricPromptInfo);
            NativeFido2AuthenticationOptions authenticationOptions = builder.build();
            //

            fido2Client.getAuthenticationIntent(authRequest, authenticationOptions,
                    new Fido2IntentCallback() {
                        @Override
                        public void onSuccess(Fido2Intent fido2Intent) {
                            // 通过Fido2Client.REGISTRATION_REQUEST，启动FIDO客户端注册流程。
                            fido2Intent.launchFido2Activity(HwIntentHelperActivity.this, Fido2Client.AUTHENTICATION_REQUEST);
                        }

                        @Override
                        public void onFailure(int errorCode, CharSequence errString) {
                            Log.d(TAG, "auth intent get failed, errorCode:" + errorCode + " errString:" + errString);
                            // return err
                            hwException = new HwException(HwException.ERR_HW_START + errorCode, errString);
                            unlock();
                        }
                    });
        } else {
            Log.e(TAG, "unknown request type");
            // return err
            hwException = new HwException(HwException.ERR_UNKNOWN_REQUEST, null);
            unlock();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                // Receive registration response
                case Fido2Client.REGISTRATION_REQUEST:
                    regResponse = fido2Client.getFido2RegistrationResponse(data);
                    break;

                // Receive authentication response
                case Fido2Client.AUTHENTICATION_REQUEST:
                    authResponse = fido2Client.getFido2AuthenticationResponse(data);
                    break;

                default:
                    hwException = new HwException(HwException.ERR_UNKNOWN, "unknown request");
            }
        } else {
            // return err
            hwException = new HwException(HwException.ERR_UNKNOWN, "unknown request");
        }

        HwIntentHelperActivity.this.finish();
        unlock();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "user force back");

        unlock();
    }

}
