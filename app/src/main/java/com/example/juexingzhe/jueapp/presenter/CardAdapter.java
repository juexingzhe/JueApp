package com.example.juexingzhe.jueapp.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.juexingzhe.jueapp.R;
import com.example.juexingzhe.jueapp.adapter.ImageViewHolder;
import com.example.juexingzhe.jueapp.adapter.LoadMoreAdapter;
import com.example.juexingzhe.jueapp.bean.PngInfoBean;

import java.util.List;

public class CardAdapter extends LoadMoreAdapter<PngInfoBean> {

    public CardAdapter(Context context, List<PngInfoBean> data) {
        super(context, data);
    }

    @Override
    public int getImageResourceId() {
        return R.layout.item;
    }

    @Override
    public int getVideoResourceId() {
        return -1;
    }

    @Override
    public RecyclerView.ViewHolder getImageViewHolder(View itemView) {
        return new ImageViewHolder(itemView);
    }

    @Override
    public RecyclerView.ViewHolder getVideoViewHolder(View itemView) {
        return null;
    }
}
