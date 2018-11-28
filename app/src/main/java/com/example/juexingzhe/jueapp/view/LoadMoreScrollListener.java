package com.example.juexingzhe.jueapp.view;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.juexingzhe.jueapp.adapter.BaseLoadAdapter;

public class LoadMoreScrollListener extends RecyclerView.OnScrollListener {
    /**
     * 预加载的提前个数
     */
    private int spans;

    private PositionChangeListener positionListener;

    private boolean isUp = false;


    public LoadMoreScrollListener(int spans) {
        this.spans = spans;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        isUp = dy < 0;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE
                || newState == RecyclerView.SCROLL_STATE_SETTLING) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            BaseLoadAdapter adapter = (BaseLoadAdapter) recyclerView.getAdapter();

            if (null == layoutManager) {
                return;
            }

            if (layoutManager instanceof GridLayoutManager) {
                int lastVisibleItemPosition =
                        ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItemPosition != -1 && positionListener != null) {
                    positionListener.currentFocusPosition(lastVisibleItemPosition);
                }

                // 最后一项说明已经可以看见最后一项了，需要加载loading框
                if (lastVisibleItemPosition != -1
                        && adapter.getItemCount() != 0
                        && (adapter.getItemCount() - spans) <= lastVisibleItemPosition
                        && !isUp
                        && adapter.getState() != BaseLoadAdapter.STATE_NOMORE) {
                    if (!adapter.isLoading()) {
                        adapter.loadMore();
                    }
                }
            }
        }
    }

    public void setPositionListener(PositionChangeListener positionListener) {
        this.positionListener = positionListener;
    }

    /**
     * 主要用于当前表情帝的item pos通知
     */
    public interface PositionChangeListener {
        void currentFocusPosition(int pos);
    }
}
