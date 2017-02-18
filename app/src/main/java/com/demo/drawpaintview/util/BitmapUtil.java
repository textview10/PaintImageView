package com.demo.drawpaintview.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by xu.wang on 2017/2/18.
 */

public class BitmapUtil {
    /**
     * 获得一个放大或者缩小后的bitmap
     * @param bitmap
     * @param bitmapWidth
     * @param bitmapHeight
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bitmap, int bitmapWidth, int bitmapHeight) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float sx = (float) bitmapWidth / w;
        float sy = (float) bitmapHeight / h;
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, w,
                h, matrix, true);
        return resizeBmp;
    }
}
