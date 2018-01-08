package com.demo.drawpaintview.paint.util;

import android.graphics.Color;
import android.util.Log;

/**
 * Created by xu.wang
 * Date on 2017/6/1 15:05
 */

public class ColorUtil {
    public static String convertColorToString(int color) {
        StringBuilder sb = new StringBuilder();
        int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        sb.append(alpha).append(",").append(red).append(",").append(green).append(",").append(blue);
        return sb.toString();
    }

    public static int convertStringToColor(String color) {
        int tempColor = Color.RED;
        try {
            String[] split = color.split(",");
            tempColor = Color.argb(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
                    Integer.parseInt(split[2]), Integer.parseInt(split[3]));

        } catch (Exception e) {
            Log.e("转化颜色异常", "....");
        }
        return tempColor;
    }

    public static int convertColorToString(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    /**
     * 将int的color转换为String #000000这种格式
     *
     * @param color
     * @return
     */
    public static String convertColorToRemote(int color) {
        StringBuffer sb = new StringBuffer();
        sb.append("#");
        sb.append(String.format("%02x",Color.alpha(color)));
        sb.append(String.format("%02x",Color.red(color)));
        sb.append(String.format("%02x",Color.green(color)));
        sb.append(String.format("%02x",Color.blue(color)));
        return sb.toString();
    }
}
