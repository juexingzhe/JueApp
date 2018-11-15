package com.example.juexingzhe.jueapp.contract;

import android.view.ViewGroup;

public interface IContract {
    interface Presenter{
        void initData();
        void setViewData(android.view.View view);
    }
    interface View{
        void showLoading();

        void hideLoading();

        void showData(String data);

        void showFailure(String msg);

        void showError(String msg);

        android.view.View getView(ViewGroup viewGroup);

        android.view.View getDelegateView();
    }
}
