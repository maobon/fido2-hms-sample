package com.gmrz.fido2.utils;

import android.util.Log;

/**
 * Created by xin on 20/12/2017.
 * Logcat print verbose info
 * 支持打印超长报文 FIDO Client DISCOVERY response
 */

public class VerboseLogger {

    public static void print(String tag, String info) {
        info = info.trim();
        int index = 0;
        int maxLength = 4000;
        String sub;

        while (index < info.length()) {
            if (info.length() <= index + maxLength) {
                sub = info.substring(index);
            } else {
                sub = info.substring(index, maxLength + index);
            }
            index += maxLength;
            Log.e(tag, sub.trim());
        }
    }
}
