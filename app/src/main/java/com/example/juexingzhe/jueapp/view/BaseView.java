package com.example.juexingzhe.jueapp.view;

import android.content.Context;
import com.example.juexingzhe.jueapp.contract.IContract;

public abstract class BaseView implements IContract.View {

    protected Context context;

    public BaseView(Context context) {
        this.context = context;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showData(String data) {

    }

    @Override
    public void showFailure(String msg) {

    }

    @Override
    public void showError(String msg) {

    }
}
