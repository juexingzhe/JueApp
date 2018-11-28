package com.example.juexingzhe.jueapp.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.juexingzhe.jueapp.R;
import com.example.juexingzhe.jueapp.util.CardConfig;

public class SlideView extends BaseView {

    private View baseView;
    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecyclerView;

    public SlideView(Context context) {
        super(context);
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler);
        mRefresh = view.findViewById(R.id.refresh);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(context, "刷新", Toast.LENGTH_SHORT).show();
                mRefresh.setRefreshing(false);
            }
        });
        mRefresh.setEnabled(CardConfig.canRefresh);
    }

    @Override
    public View getView(ViewGroup viewGroup) {
        baseView = LayoutInflater.from(context).inflate(R.layout.slide_recycler, viewGroup, false);
        Region region = new Region(new Rect(0, 0, 500, 500));

        initView(baseView);
        return baseView;
    }

    @Override
    public RecyclerView getDelegateView() {
        return mRecyclerView;
    }
}
