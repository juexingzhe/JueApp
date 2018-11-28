package com.example.juexingzhe.jueapp.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Handler
 */
public class HandlerUtil {

    private static Handler uiHandler;

    private static HandlerExecutor executor = new HandlerExecutor();

    public static HandlerExecutor getExecutor() {
        return executor;
    }

    private static Handler getHandler() {
        if (null == uiHandler) {
            synchronized (HandlerUtil.class) {
                if (null == uiHandler) {
                    uiHandler = new Handler(Looper.getMainLooper());
                }
            }
        }
        return uiHandler;
    }

    public static class HandlerExecutor implements Executor {

        @Override
        public void execute(@NonNull Runnable runnable) {
            getHandler().post(runnable);
        }

        public void executeDelay(@NonNull Runnable runnable, long delayMillis) {
            getHandler().postDelayed(runnable, delayMillis);
        }

        public void executeAtTime(@NonNull Runnable runnable, long uptimeMillis) {
            getHandler().postAtTime(runnable, uptimeMillis);
        }
    }




}
