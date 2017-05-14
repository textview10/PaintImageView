package com.demo.drawpaintview.util;

import android.graphics.Color;

/**
 * Created by：xu.wang on 2017/5/14 10:55
 */

public class ColorUtil {
    public static int convertColorToString(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }
    public static String convertColorToString(int color) {
        StringBuilder sb = new StringBuilder();
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        sb.append(alpha).append(",").append(red).append(",").append(green).append(",").append(blue);
        return sb.toString();
    }
}
