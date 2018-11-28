package com.example.juexingzhe.jueapp.bean;

import com.example.juexingzhe.jueapp.util.IConstants;

public class PngInfoBean implements IBaseInfoBean {

    private long id;
    private String imageUrl;

    public PngInfoBean(long id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String getVideoUrl() {
        return null;
    }

    @Override
    public int getType() {
        return IConstants.PNG_TYPE;
    }
}
