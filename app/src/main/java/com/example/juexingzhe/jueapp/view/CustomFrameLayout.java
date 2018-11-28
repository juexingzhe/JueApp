package com.example.juexingzhe.jueapp.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Set;

public class CustomFrameLayout extends FrameLayout {

    private static final int OBSERVER_CAPACITY = 5;

    private Paint paint;

    private HashMap<Region, View> observers;
    private HashMap<Region, EventInfo> observerEvents;


    private RegionInfo pendingRegionInfo;
    private View pendingView;
    private EventInfo pendingEventInfo;

    public CustomFrameLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);

        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (pendingRegionInfo == null) {
                    return;
                }

                pendingRegionInfo.getRect();

                if (pendingView != null) {
                    if (observers.containsValue(pendingView)) {
                        return;
                    }

                    observers.put(new Region(pendingRegionInfo.rect), pendingView);
                } else if (pendingEventInfo != null) {
                    if (observerEvents.containsValue(pendingEventInfo)) {
                        return;
                    }
                    observerEvents.put(new Region(pendingRegionInfo.rect), pendingEventInfo);
                }
            }
        });
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (observers != null && !observers.isEmpty()) {
            Set<Region> regions = observers.keySet();
            for (Region region : regions) {
                canvas.drawRect(region.getBounds(), paint);
            }
        }
    }

    public void registerObserver(View view, RegionInfo regionInfo) {
        if (observers == null) {
            observers = new HashMap<>(OBSERVER_CAPACITY);
        }

        if (observers.containsValue(view)) {
            return;
        }

        this.pendingRegionInfo = regionInfo;
        this.pendingView = view;
    }

    public void registerEvent(EventInfo event, RegionInfo regionInfo) {
        if (observerEvents == null) {
            observerEvents = new HashMap<>(OBSERVER_CAPACITY);
        }

        if (observerEvents.containsKey(event)) {
            return;
        }

        this.pendingEventInfo = event;
        this.pendingRegionInfo = regionInfo;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (!(observers == null || observers.isEmpty())) {
            Set<Region> regions = observers.keySet();
            for (Region region : regions) {
                if (region.contains((int) e.getX(), (int) e.getY())) {
                    if (e.getAction() == MotionEvent.ACTION_UP) {
                        if (observers.get(region) != null) {
                             observers.get(region).performClick();
                        }
                    }

                     return true;
                }
            }
        }

        return super.onTouchEvent(e);
    }

    @Override
    public boolean performClick() {
        super.performClick();

        return true;
    }

    public static class EventInfo {
        String tag;
        Object event;

        public EventInfo(String tag, Object event) {
            this.tag = tag;
            this.event = event;
        }
    }

    public class RegionInfo {
        int gravity;
        int width;
        int height;
        Rect rect = new Rect();

        public RegionInfo(int gravity, int width, int height) {
            this.gravity = gravity;
            this.width = width;
            this.height = height;
        }

        public void getRect() {
            switch (gravity) {
                case (Gravity.LEFT | Gravity.TOP):
                    rect.left = getLeft();
                    rect.top = getTop();
                    rect.right = getLeft() + width;
                    rect.bottom = getTop() + height;
                    break;
                case (Gravity.LEFT | Gravity.BOTTOM):
                    rect.left = getLeft();
                    rect.top = getBottom() - height;
                    rect.right = getLeft() + width;
                    rect.bottom = getBottom();
                    break;
                case (Gravity.RIGHT | Gravity.TOP):
                    rect.left = getRight() - width;
                    rect.top = getTop();
                    rect.right = getRight();
                    rect.bottom = getTop() + height;
                    break;
                case (Gravity.RIGHT | Gravity.BOTTOM):
                    rect.left = getRight() - width;
                    rect.top = getTop();
                    rect.right = getRight();
                    rect.bottom = getBottom();
                    break;
                default:
                    break;
            }
        }
    }
}
