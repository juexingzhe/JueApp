package com.example.juexingzhe.jueapp.util;

import android.text.TextUtils;
import android.util.Log;

public class LogUtils {
    private static final String LAYOUT_TAG = "SildeSnap";

    public static void LogSlide(String feature, String[] params, Object... values) {
        if (!TextUtils.isEmpty(feature)) {
            Log.i(LAYOUT_TAG,  "===========" + feature + "==========" + '\n');
        }

        if (params == null || params.length == 0) {
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (values == null || values.length == 0) {
            for (String param : params) {
                stringBuilder.append(param);
            }
        } else {
            for (int i = 0; i < params.length; i++) {
                stringBuilder.append(params[i]).append(" = ").append(values[i]);
                if (i != params.length - 1) {
                    stringBuilder.append(", ");
                }
            }
        }
        Log.i(LAYOUT_TAG, stringBuilder.toString());
    }
}
