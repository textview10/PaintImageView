package com.demo.drawpaintview.paint.util;

import android.graphics.PointF;

/**
 * Created by xu.wang
 * Date on  2017/11/7 11:01:28.
 *
 * @Desc 画板的数学类
 */

public class PaintMathUtils {
    /**
     * @param p1 第一个PointF点
     * @param p2 第二个Point点
     * @return
     * @Desc 获得两点间的距离
     */
    public static float getDistance(PointF p1, PointF p2) {
        return (float) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }


    public static PointF getBesPoint(float prex, float x, float preY, float y) {
        return new PointF(getBesValue(prex, x), getBesValue(preY, y));
    }

    public static PointF getBesPoint(PointF startP, PointF endP) {
        return getBesPoint(startP.x, endP.x, startP.y, endP.y);
    }

    /**
     * @Desc 获得上一个点为当前贝塞尔曲线的控制点, 本算法计算出的点为贝塞尔曲线结束点
     */
    public static float getBesValue(float preValue, float value) {
        return (preValue + value) / 2;
    }

    /**
     * @param circleCenterPointF 圆心
     * @param radius             半径
     * @param p                  某个点
     * @return
     * @Desc 判断某个点是否在圆的范围内
     */
    public static boolean isInCircle(PointF circleCenterPointF, int radius, PointF p) {
        return getDistance(circleCenterPointF, p) <= radius ? true : false;
    }

    /**
     * @param p1
     * @param p2
     * @return
     * @Desc 判断两个点是否相等
     */
    public static boolean eqaulPoint(PointF p1, PointF p2) {
        return p1.x == p2.x && p1.y == p2.y;
    }

    /**
     * @param t          贝塞尔公式中的t
     * @param startValue 起始点的x or y
     * @param ctrlValue  控制点的x or y
     * @param endValue   结束点的x or y
     * @return 根据以上值求出的贝塞尔曲线上的x or y
     * @Desc 贝塞尔的公式 B(t) = (1- t) * (1 -t) * P0 + 2 * t * (1 -t) * P1 + t * t * P2;  P0是起始点 p1是控制点,p2是结束点
     */
    public static float getBezierValue(float t, float startValue, float ctrlValue, float endValue) {
        return (float) (Math.pow((1 - t), 2) * startValue + 2 * t * (1 - t) * ctrlValue + Math.pow(t, 2) * endValue);
    }

    /**
     * @param touchValue
     * @param startValue
     * @param ctrlValue
     * @param endValue
     * @return 如果返回length == 1 :
     * @Desc
     */
    public static float[] getBezierT(float touchValue, float startValue, float ctrlValue, float endValue) {
        float t1, t2;
        float[] temp;
        float a = startValue - 2 * ctrlValue + endValue;
        float b = 2 * ctrlValue - 2 * startValue;
        float c = startValue - touchValue;
        if (a == 0) {
            temp = new float[1];
            t1 = -c / b;
            temp[0] = t1;
        } else {
            temp = new float[2];
            t1 = (float) ((-b + Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a));
            t2 = (float) ((-b - Math.sqrt(Math.pow(b, 2) - 4 * a * c)) / (2 * a));
            if (t1 <= 1 && t1 >= 0) {
                temp[0] = t1;
            }

            if (t2 <= 1 && t2 >= 0) {
                temp[1] = t2;
            }
        }
        return temp;
    }

}
