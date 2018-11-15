package com.example.juexingzhe.jueapp.model;

import java.util.ArrayList;
import java.util.List;

public class SlideModel {

    private List<String> mData;

    public List<String> fetchData(int num) {
        if (mData == null){
            mData = new ArrayList<>(num);
        }
        for (int i = 0; i < num; i++){
            mData.add(String.valueOf(i));
        }

        return mData;
    }

}
