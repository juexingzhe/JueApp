package com.example.juexingzhe.jueapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.VideoView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.example.juexingzhe.jueapp.R;
import com.example.juexingzhe.jueapp.global.JueApp;

public class VideoActivity extends AppCompatActivity {

    private static final String VIDEO_URL = "https://imeres.baidu.com/aremoji/2018-12-10/a41d5fa0a3d4f45777491e933b31db5e%E6%88%B4%E7%8F%8D%E7%8F%A0%E8%80%B3%E7%8E%AF%E7%9A%84%E7%86%8A%E7%8C%AB1002.mp4";
    private static final String VIDEO_URL_02 = "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4";

    private Button button;
    private VideoView videoView;
    private VideoView secondVideoView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        initView();
    }

    private void initView() {
        button = findViewById(R.id.startVideo);
        button.setOnClickListener(v -> {
            HttpProxyCacheServer proxy = JueApp.getProxy(this);

            String proxyUrl = proxy.getProxyUrl(VIDEO_URL);
            videoView.setVideoPath(proxyUrl);

            String proxyUrl1 = proxy.getProxyUrl(VIDEO_URL_02);
            secondVideoView.setVideoPath(proxyUrl1);

            videoView.start();
            secondVideoView.start();
        });

        videoView = findViewById(R.id.videoView);
        secondVideoView = findViewById(R.id.videoViewSecond);
    }
}
