package com.example.juexingzhe.jueapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.juexingzhe.jueapp.R;
import com.example.juexingzhe.jueapp.util.BitmapUtils;
import com.example.juexingzhe.jueapp.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class PhotoActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;

    private Button button;
    private ImageView imageView;

    private String currentPhotoPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        initView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                BitmapUtils.getWatermark(getApplicationContext());
            }
        }).start();
    }

    private void initView() {
        button = findViewById(R.id.captureBtn);
        imageView = findViewById(R.id.imgShower);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // dispatchTakePicIntent();
                dispatchTakeFullSizePicIntent();
            }
        });
    }

    /**
     * Dispatch Capture Thumb Image Intent
     */
    private void dispatchTakePicIntent() {
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePicIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Dispatch Take Full Size Image Intent
     */
    private void dispatchTakeFullSizePicIntent() {
        Intent takePicIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePicIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = FileUtils.createImageFile(this);
            } catch (IOException e) {
                // Error occurred while creating the File
                e.printStackTrace();
            }

            if (photoFile != null) {
                currentPhotoPath = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        getResources().getString(R.string.fileprovider_authority),
                        photoFile
                );
                takePicIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePicIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    getThumbImage(data);
                    break;
                case REQUEST_TAKE_PHOTO:
                    getFullSizeImage();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * get Thumb Image
     *
     * @param data
     */
    private void getThumbImage(Intent data) {
        Bundle extras = data.getExtras();
        if (extras == null) {
            return;
        }
        Bitmap bitmap = (Bitmap) extras.get("data");
        imageView.setImageBitmap(bitmap);
    }

    /**
     * get Full Size Image
     */
    private void getFullSizeImage() {
        if (currentPhotoPath != null) {
            BitmapUtils.rescaleSetBitmap(imageView,
                    BitmapUtils.addWatermark(imageView, currentPhotoPath).getAbsolutePath());
        }
    }
}
