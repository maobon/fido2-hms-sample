package com.gmrz.fido2.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

public class HttpDirectUtil {

    private static final int TIMEOUT_IN_MILLIONS = 5000;
    private static final String TAG = HttpDirectUtil.class.getSimpleName();

    static {
        CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
    }

    /**
     * @param url
     * @param param
     * @param contentType
     * @return
     */
    public static String doTlsPost(String url, String param, String contentType) {

        CookieManager coManager = new CookieManager();
        CookieStore cookieStore = coManager.getCookieStore();
        List<HttpCookie> cookies = cookieStore.getCookies();
        for (int i = 0; i < cookies.size(); i++) {
            HttpCookie cookie = cookies.get(i);
            Log.e(TAG, "....cookie......" + cookie);
        }

        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            // Log.e(TAG, "doTlsPost url:" + url + " param:" + param);
            VerboseLogger.print(TAG, "doTlsPost url:" + url + " param:" + param);

            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestMethod("POST");
            if (!TextUtils.isEmpty(contentType)) {
                conn.setRequestProperty("Content-Type", contentType);
            } else {
                conn.setRequestProperty("Content-Type", "application/json");
            }
            conn.setRequestProperty("charset", "utf-8");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
            conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
            if (param != null && !param.trim().equals("")) {
                out = new PrintWriter(conn.getOutputStream());
                out.print(param);
                out.flush();
            }
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            Log.v(TAG, "doTlsPost  result:" + result);
            // VerboseLogger.print(TAG, "doTlsPost  result:" + result);

        } catch (MalformedURLException e) {
            Log.e(TAG, "doTlsPost MalformedURLException e:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(TAG, "doTlsPost ioexception e:" + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "doTlsPost exception e:" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
