package com.demo.drawpaintview.paint;

import android.graphics.PointF;

/**
 * Created by xu.wang
 * Date on 2017/3/test1 11:26
 * 转化点坐标的操作类,记录原点,转化坐标
 */
public class TransPointF {
    private float bitmapWidth;
    private float bitmapHeight;
    private float centerTranX;
    private float centerTranY;

    public TransPointF(float bitmapWidth, float bitmapHeight, float centerTranX, float centerTranY) {
        this.bitmapWidth = bitmapWidth;
        this.bitmapHeight = bitmapHeight;
        this.centerTranX = centerTranX;
        this.centerTranY = centerTranY;
    }

    //逻辑坐标系转化为设备的坐标系
    public PointF logic2Display(PointF pointF) {
        PointF f = new PointF();
        float x = pointF.x * bitmapWidth / 100 + centerTranX;
        float y = pointF.y * bitmapHeight / 100 + centerTranY;
        f.x = x;
        f.y = y;
        return f;
    }

    //转化成原始相对于(100,100)图片的坐标系
    public PointF display2Logic(float x, float y) {
        PointF f = new PointF();
        if (x < centerTranX) {
            x = centerTranX;
        }
        if (x > centerTranX + bitmapWidth) {
            x = centerTranX + bitmapWidth;
        }
        if (y < centerTranY) {
            y = centerTranY;
        }
        if (y > centerTranY + bitmapHeight) {
            y = centerTranY + bitmapHeight;
        }
        f.x = (x - centerTranX) * 100 / bitmapWidth;
        f.y = (y - centerTranY) * 100 / bitmapHeight;
        return f;
    }

    //将设备坐标系内x点转化为逻辑坐标系内的y点,并发给远端
    public float display2RemoteX(float x) {
        if (x < centerTranX) {
            x = centerTranX;
        }
        if (x > centerTranX + bitmapWidth) {
            x = centerTranX + bitmapWidth;
        }
        return (x - centerTranX) * 100 / bitmapWidth;
    }

    //将设备坐标系内y点转化为逻辑坐标系内的y点,并发给远端
    public float display2RemoteY(float y) {
        if (y < centerTranY) {
            y = centerTranY;
        }
        if (y > centerTranY + bitmapHeight) {
            y = centerTranY + bitmapHeight;
        }
        return (y - centerTranY) * 100 / bitmapHeight;
    }

    //远程的点转化为逻辑坐标系内的点
    public PointF remote2Local(PointF pointF) {
        PointF temp = new PointF();
        temp.x = pointF.x;
        temp.y = pointF.y;
        return temp;
    }
}
