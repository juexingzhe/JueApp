package com.example.juexingzhe.jueapp.view;

/**
 * 表情帝UI布局信息
 */
public class PageItemViewInfo {

    private float mScaleXY;
    private int mTop;

    public PageItemViewInfo(int top, float scaleXY) {
        this.mTop = top;
        this.mScaleXY = scaleXY;
    }


    public float getScaleXY() {
        return mScaleXY;
    }

    public void setScaleXY(float mScaleXY) {
        this.mScaleXY = mScaleXY;
    }

    public int getTop() {
        return mTop;
    }

    public void setTop(int mTop) {
        this.mTop = mTop;
    }

    @Override
    public String toString() {
        return "mTop = " + mTop + ", mScaleXY = " + mScaleXY;
    }
}
