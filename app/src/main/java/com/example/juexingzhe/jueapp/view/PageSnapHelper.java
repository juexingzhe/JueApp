package com.example.juexingzhe.jueapp.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.example.juexingzhe.jueapp.util.LogUtils;

public class PageSnapHelper extends SnapHelper {

    static final float MILLISECONDS_PER_INCH = 100f;
    RecyclerView mRecyclerView;
    private int mDirection;
    // Handles the snap on scroll case.
    private final RecyclerView.OnScrollListener mScrollListener =
            new RecyclerView.OnScrollListener() {
                boolean mScrolled = false;

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    LogUtils.LogSlide("onScrollStateChanged", new String[]{"newState"}, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && mScrolled) {
                        mScrolled = false;
                        snapToTargetExistingView();
                    }
                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dx != 0 || dy != 0) {
                        mScrolled = true;
                    }
                }
            };
    private Scroller mGravityScroller;

    @Override
    public boolean onFling(int velocityX, int velocityY) {
        LogUtils.LogSlide("******onFling******", null);
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            return false;
        }
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if (adapter == null) {
            return false;
        }
        int minFlingVelocity = mRecyclerView.getMinFlingVelocity();
        return (Math.abs(velocityY) > minFlingVelocity || Math.abs(velocityX) > minFlingVelocity)
                && snapFromFling(layoutManager, velocityX, velocityY);
    }

    /**
     * Attaches the {@link SnapHelper} to the provided RecyclerView, by calling
     * {@link RecyclerView#setOnFlingListener(RecyclerView.OnFlingListener)}.
     * You can call this method with {@code null} to detach it from the current RecyclerView.
     *
     * @param recyclerView The RecyclerView instance to which you want to add this helper or
     *                     {@code null} if you want to remove SnapHelper from the current
     *                     RecyclerView.
     * @throws IllegalArgumentException if there is already a {@link RecyclerView.OnFlingListener}
     *                                  attached to the provided {@link RecyclerView}.
     */
    public void attachToRecyclerView(@Nullable RecyclerView recyclerView)
            throws IllegalStateException {
        if (mRecyclerView == recyclerView) {
            return; // nothing to do
        }
        if (mRecyclerView != null) {
            destroyCallbacks();
        }
        mRecyclerView = recyclerView;
        if (mRecyclerView != null) {
            setupCallbacks();
            mGravityScroller = new Scroller(mRecyclerView.getContext(),
                    new DecelerateInterpolator());
            LogUtils.LogSlide("attachToRecyclerView", null);
            snapToTargetExistingView();
        }
    }

    /**
     * Called when an instance of a {@link RecyclerView} is attached.
     */
    private void setupCallbacks() throws IllegalStateException {
        if (mRecyclerView.getOnFlingListener() != null) {
            throw new IllegalStateException("An instance of OnFlingListener already set.");
        }
        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.setOnFlingListener(this);
    }

    /**
     * Called when the instance of a {@link RecyclerView} is detached.
     */
    private void destroyCallbacks() {
        mRecyclerView.removeOnScrollListener(mScrollListener);
        mRecyclerView.setOnFlingListener(null);
    }

    /**
     * Calculated the estimated scroll distance in each direction given velocities on both axes.
     *
     * @param velocityX Fling velocity on the horizontal axis.
     * @param velocityY Fling velocity on the vertical axis.
     * @return array holding the calculated distances in x and y directions
     * respectively.
     */
    public int[] calculateScrollDistance(int velocityX, int velocityY) {
        int[] outDist = new int[2];
        mGravityScroller.fling(0, 0, velocityX, velocityY,
                Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
        outDist[0] = mGravityScroller.getFinalX();
        outDist[1] = mGravityScroller.getFinalY();
        return outDist;
    }

    /**
     * Helper method to facilitate for snapping triggered by a fling.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @param velocityX     Fling velocity on the horizontal axis.
     * @param velocityY     Fling velocity on the vertical axis.
     * @return true if it is handled, false otherwise.
     */
    private boolean snapFromFling(@NonNull RecyclerView.LayoutManager layoutManager, int velocityX,
                                  int velocityY) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return false;
        }

        RecyclerView.SmoothScroller smoothScroller = createScroller(layoutManager);
        if (smoothScroller == null) {
            return false;
        }

        int targetPosition = findTargetSnapPosition(layoutManager, velocityX, velocityY);
        if (targetPosition == RecyclerView.NO_POSITION) {
            return false;
        }

        smoothScroller.setTargetPosition(targetPosition);
        layoutManager.startSmoothScroll(smoothScroller);
        return true;
    }

    /**
     * Snaps to a target view which currently exists in the attached {@link RecyclerView}. This
     * method is used to snap the view when the {@link RecyclerView} is first attached; when
     * snapping was triggered by a scroll and when the fling is at its final stages.
     */
    void snapToTargetExistingView() {
        LogUtils.LogSlide("snapToTargetExistingView", null);
        if (mRecyclerView == null) {
            return;
        }
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        View snapView = findSnapView(layoutManager);
        if (snapView == null) {
            return;
        }
        int[] snapDistance = calculateDistanceToFinalSnap(layoutManager, snapView);
        if (snapDistance[0] != 0 || snapDistance[1] != 0) {
            mRecyclerView.smoothScrollBy(snapDistance[0], snapDistance[1]);
        }
    }

    /**
     * Creates a scroller to be used in the snapping implementation.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @return a {@link RecyclerView.SmoothScroller} which will handle the scrolling.
     */
    @Nullable
    protected RecyclerView.SmoothScroller createScroller(RecyclerView.LayoutManager layoutManager) {
        return createSnapScroller(layoutManager);
    }

    /**
     * Creates a scroller to be used in the snapping implementation.
     *
     * @param layoutManager The {@link RecyclerView.LayoutManager} associated with the attached
     *                      {@link RecyclerView}.
     * @return a {@link LinearSmoothScroller} which will handle the scrolling.
     * @deprecated use {@link #createScroller(RecyclerView.LayoutManager)} instead.
     */
    @Nullable
    @Deprecated
    protected LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager layoutManager) {
        if (!(layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
            return null;
        }
        return new LinearSmoothScroller(mRecyclerView.getContext()) {
            @Override
            protected void onTargetFound(View targetView, RecyclerView.State state, Action action) {
                LogUtils.LogSlide("onTargetFound", new String[]{"targetView"}, targetView.getTag());
                int[] snapDistances = calculateDistanceToFinalSnap(mRecyclerView.getLayoutManager(),
                        targetView);
                final int dx = snapDistances[0];
                final int dy = snapDistances[1];
                final int time = calculateTimeForDeceleration(Math.max(Math.abs(dx), Math.abs(dy)));
                if (time > 0) {
                    action.update(dx, dy, time, mDecelerateInterpolator);
                }
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };
    }

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(
            @NonNull RecyclerView.LayoutManager layoutManager,
            @NonNull View targetView) {
        LogUtils.LogSlide("calculateDistanceToFinalSnap", null);
        if (layoutManager instanceof SlidePageLayoutManager) {
            int[] out = new int[2];
            if (layoutManager.canScrollHorizontally()) {
                out[0] = ((SlidePageLayoutManager) layoutManager).calculateDistanceToPosition(
                        layoutManager.getPosition(targetView));
                out[1] = 0;
            } else {
                out[0] = 0;
                out[1] = ((SlidePageLayoutManager) layoutManager).calculateDistanceToPosition(
                        layoutManager.getPosition(targetView));
            }
            LogUtils.LogSlide(null, new String[]{"y"}, out[1]);
            return out;
        }
        return null;
    }


    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        LogUtils.LogSlide("findSnapView", null);
        if (layoutManager instanceof SlidePageLayoutManager) {
            int pos = ((SlidePageLayoutManager) layoutManager).getFixedScrollPosition(mDirection);
            LogUtils.LogSlide(null, new String[]{"pos"}, pos);
            mDirection = 0;
            if (pos != RecyclerView.NO_POSITION) {
                View view = layoutManager.findViewByPosition(pos);
                LogUtils.LogSlide(null, new String[]{"snapView"}, view.getTag());
                return view;
            }
        }
        return null;
    }

    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        LogUtils.LogSlide("findTargetSnapPosition", null);
        if (layoutManager.canScrollVertically()) {
            mDirection = velocityY;
            return ((SlidePageLayoutManager) layoutManager).getFixedScrollPosition(mDirection);
        } else {
            mDirection = velocityX;
        }

        return RecyclerView.NO_POSITION;
    }
}
