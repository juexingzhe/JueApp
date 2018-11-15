package com.example.juexingzhe.jueapp.util;

import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

public class PixelUtils {

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 屏幕分辨率
     *
     * @return density
     */
    public static float getDisplayDensity(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.density;
    }

    /**
     * 是否横屏
     *
     * @return
     */
    public static boolean isLandScape(Context context) {
        // 横屏
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_LANDSCAPE;

    }

    /**
     * 屏幕宽度
     *
     * @return
     */
    public static int getWindowWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    /**
     * 屏幕高度
     *
     * @return
     */
    public static int getWindowHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

}
