package com.example.juexingzhe.jueapp.presenter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.juexingzhe.jueapp.model.SlideModel;
import com.example.juexingzhe.jueapp.view.SlidePageLayoutManager;
import com.example.juexingzhe.jueapp.view.SlideView;

public class SlidePresenter extends BasePresenter<SlideView> {

    private SlideModel slideModel;
    private CardAdapter cardAdapter;

    public SlidePresenter() {
        slideModel = new SlideModel();
    }

    @Override
    public void initData() {
        cardAdapter = new CardAdapter(view.getDelegateView().getContext(), slideModel.fetchData(10));
    }

    @Override
    public void setViewData(View view) {
        if (view instanceof RecyclerView) {
            ((RecyclerView) view).setLayoutManager(new SlidePageLayoutManager(view.getContext(), cardAdapter));
            ((RecyclerView) view).setAdapter(cardAdapter);
        }
    }
}
