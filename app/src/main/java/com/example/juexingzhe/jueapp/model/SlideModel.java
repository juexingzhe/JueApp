package com.example.juexingzhe.jueapp.model;

import com.example.juexingzhe.jueapp.R;
import com.example.juexingzhe.jueapp.bean.PngInfoBean;

import java.util.ArrayList;
import java.util.List;

public class SlideModel {
    private int[] drawables = new int[]{R.drawable.first, R.drawable.second, R.drawable.third};

    private List<PngInfoBean> mData;

    public List<PngInfoBean> fetchData(int num) {
        if (mData == null){
            mData = new ArrayList<>(num);
        }
        for (int i = 0; i < num; i++){
            mData.add(new PngInfoBean(i, String.valueOf(drawables[i % drawables.length])));
        }

        return mData;
    }

}
