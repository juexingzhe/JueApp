package com.example.juexingzhe.jueapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.juexingzhe.jueapp.R;

public class ImageViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;

    public ImageViewHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.img);
    }
}
