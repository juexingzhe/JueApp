package com.example.juexingzhe.interfaces;


import com.google.auto.service.AutoService;

@AutoService(Display.class)
public class BDisplay implements Display {
    @Override
    public String display() {
        return "B Display";
    }
}
