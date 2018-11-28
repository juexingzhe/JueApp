package com.example.juexingzhe.jueapp.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.ExifInterface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.example.juexingzhe.jueapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapUtils {

    /**
     * scaled Bitmap from photoPath to match the size of the destination view
     *
     * @param targetView
     * @param photoPath
     */
    public static void rescaleSetBitmap(@NonNull ImageView targetView, @NonNull String photoPath) {

        int targetW = targetView.getWidth();
        int targetH = targetView.getHeight();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, options);
        int photoW = options.outWidth;
        int photoH = options.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        options.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        targetView.setImageBitmap(bitmap);
    }

    public static File addWatermark(@NonNull ImageView targetView, @NonNull String photoPath) {
        Resources resources = targetView.getContext().getResources();

        Bitmap watermarkBitmap = BitmapFactory.decodeResource(resources, R.drawable.logo);
        Bitmap rawBitmap = BitmapFactory.decodeFile(photoPath);

        int targetW = targetView.getWidth();
        int targetH = targetView.getHeight();

        Bitmap dstBitmap = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(dstBitmap);

        Rect watermarkRect = new Rect((int) (PixelUtils.getDisplayDensity(targetView.getContext()) * 20),
                (int) (targetH - watermarkBitmap.getHeight() - PixelUtils.getDisplayDensity(targetView.getContext()) * 20),
                (int) (watermarkBitmap.getWidth() + PixelUtils.getDisplayDensity(targetView.getContext()) * 20),
                (int) (targetH - PixelUtils.getDisplayDensity(targetView.getContext()) * 20));
        Rect rawRect = new Rect(0, 0, targetW, targetH);


        canvas.drawBitmap(rawBitmap, null, rawRect, null);
        canvas.drawBitmap(watermarkBitmap, new Rect(0, 0, watermarkBitmap.getWidth() - 20, watermarkBitmap.getHeight()), watermarkRect, null);

        File watermaskImageOutFile;
        try {
            watermaskImageOutFile = FileUtils.createWatermaskImageOutFile(targetView.getContext());
            dstBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(watermaskImageOutFile));
        } catch (IOException e) {
            e.printStackTrace();
            watermaskImageOutFile = new File(photoPath);
        }

        return watermaskImageOutFile;
    }

    /**
     * logo水印截取重新生成
     *
     * @param context
     */
    public static void getWatermark(Context context) {
        Resources resources = context.getResources();
        Bitmap watermarkBitmap = BitmapFactory.decodeResource(resources, R.drawable.logo);

        int targetW = watermarkBitmap.getWidth();
        int targetH = watermarkBitmap.getHeight();

        Bitmap dstBitmap = Bitmap.createBitmap(targetW, targetH, Bitmap.Config.ARGB_8888);
        // setBitmapBGColor(dstBitmap, Color.WHITE);
        Canvas canvas = new Canvas(dstBitmap);

        canvas.drawBitmap(watermarkBitmap,
                new Rect(0, 0, targetW - 20, targetH),
                new Rect(0, 0, targetW, targetH),
                null);

        try {
            dstBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "logo.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置位图的背景色
     *
     * @param bitmap 需要设置的位图
     * @param color  背景色
     */
    private static void setBitmapBGColor(Bitmap bitmap, int color) {
        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                bitmap.setPixel(i, j, color);//将bitmap的每个像素点都设置成相应的颜色
            }
        }
    }

    /**
     * 获取图片的旋转方向
     *
     * @param inputStream
     * @return degree
     */
    public static int getExifOrientation(InputStream inputStream) {
        int orientation;
        int digree = 0;
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                ExifInterface exifInterface = new ExifInterface(inputStream);
                orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return digree;
    }

    /**
     * 旋转图片
     *
     * @param sourceBitmap
     * @param digree
     * @return
     */
    public static Bitmap rotateBitmap(@NonNull Bitmap sourceBitmap, int digree) {
        if (digree == 0) {
            return sourceBitmap;
        }

        Matrix matrix = new Matrix();
        matrix.postRotate(digree);

        return Bitmap.createBitmap(sourceBitmap,
                0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
    }


}
