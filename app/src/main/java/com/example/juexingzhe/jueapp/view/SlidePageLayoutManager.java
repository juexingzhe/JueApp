package com.example.juexingzhe.jueapp.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.juexingzhe.jueapp.presenter.CardAdapter;
import com.example.juexingzhe.jueapp.util.LogUtils;
import com.example.juexingzhe.jueapp.util.PixelUtils;

import java.util.ArrayList;

public class SlidePageLayoutManager extends GridLayoutManager {
    private static final String TAG = SlidePageLayoutManager.class.getSimpleName() + "_log";
    private static final int VISIBLE_EMOTICON_COUNT = 3;
    private static final int ITEM_OFFSET = 20;

    private Context context;
    private int mItemCount;
    private int mScrollOffset = 0;
    private CardAdapter commonAdapter;

    private PageSnapHelper snapHelper;
    private boolean hasChild;

    private int itemWidth;
    private int itemHeight;

    private int bottomHeight;

    private RecyclerView.Recycler recycler;

    public SlidePageLayoutManager(Context context, CardAdapter commonAdapter) {
        super(context, 1);
        this.context = context;
        this.commonAdapter = commonAdapter;

        this.snapHelper = new PageSnapHelper();

        itemWidth = PixelUtils.dip2px(context, 279);
        itemHeight = PixelUtils.dip2px(context, 372);
        bottomHeight = PixelUtils.dip2px(context, 60);
    }

    @Override
    public void onAttachedToWindow(RecyclerView view) {
        super.onAttachedToWindow(view);
        this.snapHelper.attachToRecyclerView(view);
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0 || state.isPreLayout()) {
            return;
        }

        if (!hasChild) {
            hasChild = true;
        }

        mItemCount = getItemCount();
        mScrollOffset = Math.min(Math.max(0, mScrollOffset), (mItemCount - 2) * itemHeight + bottomHeight);

        this.recycler = recycler;
        layoutChild(recycler);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int pendingScrollOffset = mScrollOffset + dy;
        mScrollOffset = Math.min(Math.max(0, pendingScrollOffset), (mItemCount - 2) * itemHeight + bottomHeight);
        // Log.i(PageSnapHelper.LAYOUT_TAG, "scrollVerticallyBy ===== mScrollOffset = " + mScrollOffset + "=====");
        layoutChild(recycler);
        return mScrollOffset - pendingScrollOffset + dy;
    }


    @Override
    public boolean canScrollVertically() {
        return true;
    }

    public void refreshLayout() {
        layoutChild(recycler);
    }


    private void layoutChild(RecyclerView.Recycler recycler) {
        if (getItemCount() == 0) {
            return;
        }
        Log.i(TAG, "ScrollOffset = " + mScrollOffset + ", maxHeight = " + ((mItemCount - 2) * itemHeight + bottomHeight));
        if (mScrollOffset >= ((mItemCount - 2) * itemHeight + bottomHeight)) {
            return;
        }
        // 第一个可见Item位置
        int firstItemPosition = (int) Math.floor(mScrollOffset / itemHeight);
        Log.i(TAG, "firstItemPosition = " + firstItemPosition);
        // 如果第一个可见Item位置是最后一个Item，返回
        if (firstItemPosition >= commonAdapter.getItemCount() - 1) {
            return;
        }

        // 第一个可见Item划过的距离，也就是不可见
        int firstItemScrolledHeight = mScrollOffset % itemHeight;

        // 第一个可见Item划过的距离占自身高度的百分比
        final float firstItemScrolledHeightPercent = firstItemScrolledHeight * 1.0f / itemHeight;
        ArrayList<PageItemViewInfo> layoutInfos = new ArrayList<>();

        // 计算view位置
        int tmpCount = Math.min(VISIBLE_EMOTICON_COUNT, commonAdapter.getItemCount() - firstItemPosition - 1);
        for (int i = 0; i <= tmpCount; i++) {
            // 用于计算偏移量
            int tmp = i + 1;
            double maxOffset = (getVerticalSpace()
                    - itemHeight - firstItemScrolledHeightPercent) / 2 * Math.pow(0.65, tmp);
            if (maxOffset <= 0) {
                break;
            }
            int start;
            if (i == 0) {
                start = getPaddingTop() - firstItemScrolledHeight;
            } else {
                start = (int) (getPaddingTop() + i * maxOffset + i * ITEM_OFFSET);
            }
            float mScale = 0.95f;
            float scaleXY = (float) (Math.pow(mScale, i) * (1 - firstItemScrolledHeightPercent * (1 - mScale)));
            PageItemViewInfo info = new PageItemViewInfo(start, scaleXY);
            layoutInfos.add(0, info);
        }

        // 回收View
        int layoutCount = layoutInfos.size();
        final int endPos = firstItemPosition + VISIBLE_EMOTICON_COUNT;
        final int childCount = getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            View childView = getChildAt(i);
            if (childView == null) {
                continue;
            }
            int pos;
            try {
                pos = getPosition(childView);
            } catch (NullPointerException e) {
                e.printStackTrace();
                continue;
            }

            if (pos > endPos + 1 || pos < firstItemPosition - 1) {
                removeAndRecycleView(childView, recycler);
            }
        }

        detachAndScrapAttachedViews(recycler);

        // 添加Item
        for (int i = layoutCount - 1; i >= 0; i--) {
            int pos = firstItemPosition + i;
            if (pos > commonAdapter.getItemCount() - 1) {
                break;
            }
            // If a ViewHolder must be constructed and not enough time remains, null is returned, 不进行layout
            View view;
            try {
                view = recycler.getViewForPosition(pos);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                return;
            }

            PageItemViewInfo layoutInfo = layoutInfos.get(layoutCount - 1 - i);
            view.setTag(pos);
            addView(view);
            measureChildWithExactlySize(view);
            int left = (getHorizontalSpace() - itemWidth) / 2;
            layoutDecoratedWithMargins(view, left,
                    layoutInfo.getTop(),
                    left + itemWidth,
                    layoutInfo.getTop() + itemHeight);
            view.setPivotX(view.getWidth() / 2);
            view.setPivotY(view.getHeight() / 2);
            view.setScaleX(layoutInfo.getScaleXY());
            view.setScaleY(layoutInfo.getScaleXY());
        }

        // View view = recycler.getViewForPosition(firstItemPosition);
    }

    /**
     * 测量itemview的确切大小
     */
    private void measureChildWithExactlySize(View child) {
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(itemWidth, View.MeasureSpec.EXACTLY);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(itemHeight, View.MeasureSpec.EXACTLY);
        child.measure(widthSpec, heightSpec);
    }

    /**
     * 获取RecyclerView的显示高度
     */
    private int getVerticalSpace() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    /**
     * 获取RecyclerView的显示宽度
     */
    private int getHorizontalSpace() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    public int calculateDistanceToPosition(int targetPos) {
        int distance;
        if (targetPos >= commonAdapter.getItemCount() - 1) {
            distance = itemHeight * (targetPos - 1) - mScrollOffset;
        } else {
            distance = itemHeight * targetPos - mScrollOffset;
        }
        LogUtils.LogSlide("calculateDistanceToPosition",
                new String[]{"targetPos", "distance", "scrollOffset"},
                targetPos,
                distance,
                mScrollOffset);
        return distance;
    }

    public int getFixedScrollPosition(int direction) {
        if (hasChild) {
            LogUtils.LogSlide("getFixedScrollPosition", null);
            if (mScrollOffset % itemHeight == 0) {
                return RecyclerView.NO_POSITION;
            }
            float position = mScrollOffset * 1.0f / itemHeight;

            if (direction > 0) {
                position = (int) Math.ceil(position);
            } else if (direction < 0) {
                position = (int) Math.floor(position);
            } else {
                if ((int) position == commonAdapter.getItemCount() - 2) {
                    position = (int) Math.ceil(position);
                }
            }

            LogUtils.LogSlide(null,
                    new String[]{"ScrollOffset", "itemHeight", "position", "direction"},
                    mScrollOffset,
                    itemHeight,
                    position,
                    direction);
            return (int) position;
        }
        return RecyclerView.NO_POSITION;
    }
}
