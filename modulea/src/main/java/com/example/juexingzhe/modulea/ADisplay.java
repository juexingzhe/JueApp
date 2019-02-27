package com.example.juexingzhe.modulea;


import com.example.juexingzhe.moduleb.Display;
import com.google.auto.service.AutoService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@AutoService(Display.class)
public class ADisplay implements Display {
    @Override
    public String display() {
        EventBus.getDefault().register(this);
        return "B Display";
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ModuleaMsg msg) {
        System.out.println("Module A received msg = " + msg.msg);
        EventBus.getDefault().unregister(this);
    }
}
