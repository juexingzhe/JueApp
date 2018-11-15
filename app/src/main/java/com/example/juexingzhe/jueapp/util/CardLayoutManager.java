package com.example.juexingzhe.jueapp.util;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

public class CardLayoutManager extends LinearLayoutManager {

    private Context context;
    float translationY;
    private PagerSnapHelper pagerSnapHelper;
    private RecyclerView recyclerView;

    public CardLayoutManager(Context context, int orientation) {
        super(context, orientation, false);
        init(context);
    }

    public CardLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        CardConfig.initConfig(context);
        translationY = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
        pagerSnapHelper = new PagerSnapHelper();
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        pagerSnapHelper.attachToRecyclerView(view);
        this.recyclerView = view;
        recyclerView.addOnChildAttachStateChangeListener(childAttachStateChangeListener);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

        detachAndScrapAttachedViews(recycler);
        int itemCount = getItemCount();
        int bottomPosition;

        if (itemCount < 4) {
            bottomPosition = itemCount;
        } else {
            bottomPosition = 4;
        }

        for (int i = bottomPosition - 1; i >= 0; i--) {
            View view = recycler.getViewForPosition(i);
            addView(view);
            measureChildWithMargins(view, 0, 0);
            int viewWidth = getDecoratedMeasuredWidth(view);
            int viewHeight = getDecoratedMeasuredHeight(view);
            int widthSpace = getWidth() - viewWidth;
            int heightSpace = getHeight() - viewHeight;


            layoutDecorated(view,
                    widthSpace / 2,
                    heightSpace / 2,
                    widthSpace / 2 + viewWidth,
                    heightSpace/ 2 + viewHeight);


            view.setTranslationY(translationY * i);
            view.setScaleX(1 - CardConfig.SCALE_GAP * i);
            view.setScaleY(1 - CardConfig.SCALE_GAP * i);

        }
    }

    private RecyclerView.OnChildAttachStateChangeListener childAttachStateChangeListener = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {

        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {

        }
    };
}
