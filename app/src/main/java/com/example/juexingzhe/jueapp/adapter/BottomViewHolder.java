package com.example.juexingzhe.jueapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.juexingzhe.jueapp.R;

public class BottomViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout container;
    public TextView bottomTextView;
    public ImageView bottomIcon;
    public ProgressBar progressBar;

    public BottomViewHolder(View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.bottom_container);
        bottomTextView = itemView.findViewById(R.id.bottom_title);
        bottomIcon = itemView.findViewById(R.id.bottom_icon);
        progressBar = itemView.findViewById(R.id.progress);
    }
}
