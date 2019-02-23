package com.example.juexingzhe.jueapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.juexingzhe.jueapp.R;
import com.example.juexingzhe.jueapp.annotation.DisplayFactory;

public class AnnotationActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);

        textView = findViewById(R.id.annotation_txt);

        loadModule();
    }

    private void loadModule(){
        String display = "";
        StringBuilder stringBuilder = new StringBuilder();
        while (DisplayFactory.getSingleton().hasNextDisplay()){
            display = DisplayFactory.getSingleton().getDisplay().display();
            stringBuilder.append(display).append("\n");
        }
        textView.setText(stringBuilder.toString());
    }
}
