package com.example.juexingzhe.processor;


import com.example.juexingzhe.interfaces.Display;
import com.google.auto.service.AutoService;

@AutoService(Display.class)
public class ADisplay implements Display{
    @Override
    public String display() {
        return "A Display";
    }
}
