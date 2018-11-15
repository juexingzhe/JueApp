package com.example.juexingzhe.jueapp.util;

import android.content.Context;
import android.util.TypedValue;

public class CardConfig {

    public static boolean canRefresh = true;
    //屏幕最对同时显示几个item
    public static int  MAX_SHOW_COUNT=4;
    //没一级Scale相差0.05f，translation相差7dp左右
    public static  float SCALE_GAP;
    public static int TRANS_V_GAP;

    public static void initConfig(Context context){
        MAX_SHOW_COUNT=4;
        SCALE_GAP=0.05f;
        TRANS_V_GAP=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,15,context.getResources().getDisplayMetrics());
    }
}
