package com.example.juexingzhe.jueapp.presenter;


import com.example.juexingzhe.jueapp.contract.IContract;

public abstract class BasePresenter<V extends IContract.View> implements IContract.Presenter {

    protected V view;

    public BasePresenter() {
    }

    public void attachView(V view){
        this.view = view;
    }

    public void destroyView(){
        this.view = null;
    }

    protected boolean isViewAttached(){
        return this.view != null;
    }
}
