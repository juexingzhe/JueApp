package com.example.juexingzhe.jueapp.activity;

import android.graphics.Rect;
import android.graphics.Region;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.juexingzhe.jueapp.R;
import com.example.juexingzhe.jueapp.util.PixelUtils;
import com.example.juexingzhe.jueapp.view.CustomFrameLayout;

public class RegionActivity extends AppCompatActivity {

    RelativeLayout relativeLayout;
    private Button regionAnchor;
    private Button clickBtn;
    private Rect rect;
    private Region region;
    private View contentView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_region);

        initView();
    }

    private void initView() {
        relativeLayout = findViewById(R.id.region_container);
        regionAnchor = findViewById(R.id.region_anchor);
        clickBtn = findViewById(R.id.region_click);
        clickBtn.setGravity(Gravity.LEFT | Gravity.BOTTOM);

        regionAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regionAnchor.setVisibility(View.GONE);
                final PopupWindow popupWindow = new PopupWindow(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                popupWindow.setBackgroundDrawable(null);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setTouchable(true);
                popupWindow.setContentView(contentView);
                popupWindow.setClippingEnabled(false);
                popupWindow.showAsDropDown(regionAnchor, 0, 0);
            }
        });
        clickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "On Click", Toast.LENGTH_SHORT).show();
            }
        });
        clickBtn.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getContentView();
            }
        });
    }

    private void getContentView() {
        CustomFrameLayout customFrameLayout = new CustomFrameLayout(getBaseContext());
        Button button = new Button(getBaseContext());
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = PixelUtils.dip2px(getBaseContext(), 60);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        button.setText(R.string.pop_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "In Pop Btn", Toast.LENGTH_SHORT).show();
            }
        });
        customFrameLayout.addView(button, layoutParams);
        customFrameLayout.setBackground(getResources().getDrawable(R.drawable.item_bg, null));
        rect = new Rect(clickBtn.getLeft(),
                clickBtn.getTop() + PixelUtils.dip2px(getBaseContext(), 30),
                clickBtn.getRight(),
                clickBtn.getBottom() + PixelUtils.dip2px(getBaseContext(), 30));
        region = new Region(rect);

        customFrameLayout.registerObserver(clickBtn,  customFrameLayout.new RegionInfo((Gravity.LEFT | Gravity.BOTTOM),
                clickBtn.getWidth(), clickBtn.getHeight()));

        contentView = customFrameLayout;
    }

}
