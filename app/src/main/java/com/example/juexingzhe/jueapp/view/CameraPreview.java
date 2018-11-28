package com.example.juexingzhe.jueapp.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.juexingzhe.jueapp.R;
import com.example.juexingzhe.jueapp.util.BitmapUtils;
import com.example.juexingzhe.jueapp.util.WorkerManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class CameraPreview extends FrameLayout implements TextureView.SurfaceTextureListener {

    private TextureView textureView;
    private Context context;
    private ImageView coverImg;

    private Camera mCamera;
    private CountDownLatch latch;

    /**
     * 预览界面的旋转角度
     */
    private int rotationDigree = 0;

    public CameraPreview(@NonNull Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public CameraPreview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public CameraPreview(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initTextureView();
        }

        initImg();

        latch = new CountDownLatch(1);
    }

    private void initImg() {
        if (null == coverImg) {
            coverImg = new ImageView(context);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            coverImg.setScaleType(ImageView.ScaleType.FIT_XY);
            addView(coverImg, params);
        }
    }

    private void initTextureView() {
        if (null == textureView) {
            textureView = new TextureView(context);
            textureView.setKeepScreenOn(true);
            textureView.setSurfaceTextureListener(this);
        }
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        addView(textureView, layoutParams);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        initImg();

        coverImg.setImageResource(R.drawable.second);
        final Camera[] camera = new Camera[1];

        WorkerManager.getInstance().postTask(new Runnable() {
            @Override
            public void run() {
                camera[0] = safeCameraOpen(Camera.CameraInfo.CAMERA_FACING_BACK);
            }
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setCamera(surface, camera[0]);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(width, height);
        requestLayout();
        mCamera.setParameters(parameters);

        // Important: Call startPreview() to start updating the preview surface.
        // Preview must be started before you can take a picture.
        mCamera.startPreview();
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        stopPreviewAndFreeCamera();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    /**
     * executed in a Thread
     *
     * @param id
     * @return
     */
    private Camera safeCameraOpen(final int id) {
        Camera camera;

        try {
            releaseCameraAndPreview();
            camera = Camera.open(id);
        } catch (Exception e) {
            e.printStackTrace();
            camera = null;
        }

        latch.countDown();

        return camera;
    }

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


    private void setCamera(SurfaceTexture surface, Camera camera) {
        if (mCamera == camera) {
            return;
        }

        stopPreviewAndFreeCamera();

        mCamera = camera;

        if (mCamera != null) {
            setCameraDisplayOrientation(Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
            if (coverImg != null) {
                coverImg.setVisibility(GONE);
            }
            Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
            textureView.setLayoutParams(new FrameLayout.LayoutParams(previewSize.width,
                    previewSize.height, Gravity.CENTER));

            try {
                mCamera.setPreviewTexture(surface);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mCamera.startPreview();
        }

    }

    /**
     * If you want to make the camera image show in the same orientation as the display, you can use the following code.
     */
    public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = ((Activity) getContext()).getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        rotationDigree = result;
        camera.setDisplayOrientation(rotationDigree);
    }

    /**
     * When this function returns, mCamera will be null.
     */
    private void stopPreviewAndFreeCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();

            mCamera.release();

            mCamera = null;
        }
    }

    public void startPreview() {
        initTextureView();
    }

    /**
     * 拍照片
     */
    public void takePicture() {
        if (mCamera != null) {
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {

                    if (coverImg == null) {
                        return;
                    }

                    if (data == null || data.length == 0) {
                        return;
                    }

                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (bitmap != null) {
                        coverImg.setVisibility(VISIBLE);
                        coverImg.setImageBitmap(BitmapUtils.rotateBitmap(bitmap, (180 - rotationDigree)));
                        stopPreviewAndFreeCamera();
                    }
                }
            });
        }
    }


}
