package com.example.juexingzhe.moduleb;

import com.google.auto.service.AutoService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

@AutoService(Display.class)
public class BDisplay implements Display {
    @Override
    public String display() {
        EventBus.getDefault().register(this);
        return "B Display";
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ModulebMsg msg) {
        System.out.println("Module B received msg = " + msg.num);
        EventBus.getDefault().unregister(this);
    }
}
