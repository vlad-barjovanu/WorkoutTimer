package com.vbarjovanu.workouttimer.helpers.images;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public class BitmapHelper {

    /**
     * Creates a bitmap from a image file
     * @param filePath image file's filepath
     * @return Bitmap
     */
    public static Bitmap fromFilePath(String filePath) {
        Bitmap imageBitmap = null;
        File imgFile = new File(filePath);
        if (imgFile.exists()) {
            imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }

        return imageBitmap;
    }

    /**
     * Creates a bitmap from a drawable resource
     * @param context android context
     * @param resourceId drawable resource ID
     * @return Bitmap
     */
    public static Bitmap fromResource(Context context, int resourceId){
        return BitmapFactory.decodeResource(context.getResources(), resourceId);
    }
}
