package com.example.juexingzhe.jueapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.juexingzhe.jueapp.R;
import com.example.juexingzhe.jueapp.presenter.SlidePresenter;
import com.example.juexingzhe.jueapp.view.SlideView;

public class MainActivity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener {

    private SlideView view;
    private SlidePresenter presenter;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setData();

    }

    private void initView() {
        myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.inflateMenu(R.menu.main_menu);
        myToolbar.setTitle(getResources().getString(R.string.app_name));

        myToolbar.setOnMenuItemClickListener(this);

        FrameLayout container = findViewById(R.id.container);

        //建立View与Presenter关系
        presenter = new SlidePresenter();
        view = new SlideView(this);
        presenter.attachView(view);

        //添加View到容器
        container.addView(view.getView(container));
    }

    private void setData() {
        presenter.initData();
        presenter.setViewData(view.getDelegateView());
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.action_photo:
                // User chose the "Settings" item, show the app settings UI...
                intent = new Intent(this, PhotoActivity.class);
                startActivity(intent);
                break;
            case R.id.action_video:
                intent = new Intent(this, VideoActivity.class);
                startActivity(intent);
                break;
            case R.id.action_camera:
                intent = new Intent(this, CameraActivity.class);
                startActivity(intent);
                break;
            case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                Toast.makeText(MainActivity.this, "Favorite !", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }
}
