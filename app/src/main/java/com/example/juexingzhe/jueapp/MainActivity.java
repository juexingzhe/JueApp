package com.example.juexingzhe.jueapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.example.juexingzhe.jueapp.presenter.SlidePresenter;
import com.example.juexingzhe.jueapp.view.SlideView;

public class MainActivity extends AppCompatActivity {

    private SlideView view;
    private SlidePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setData();

    }

    private void initView() {
        FrameLayout container = findViewById(R.id.container);

        //建立View与Presenter关系
        presenter = new SlidePresenter();
        view = new SlideView(this);
        presenter.attachView(view);

        //添加View到容器
        container.addView(view.getView(container));
    }

    private void setData() {
        presenter.initData();
        presenter.setViewData(view.getDelegateView());
    }
}
