package com.demo.drawpaintview.paint.util;

import android.graphics.PointF;

/**
 * Created by xu.wang
 * Date on 2017/3/1 11:26
 * 转化点坐标的操作类,记录原点,转化坐标
 * display 设备坐标系 , 图片相对于屏幕的坐标系,加上了图片居中显示距离左侧或者右侧的距离
 * origin  原始坐标系 , 相对于图片左顶点的坐标系
 * logic 逻辑坐标系  ,  原始坐标系根据图片width,height,转换而成的相对于(100,100)的坐标系
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

    public PointF logic2Origin(PointF logicP) {
        return new PointF(logicP.x * bitmapWidth / 100, logicP.y * bitmapHeight / 100);
    }

    //转化成原始相对于(100,100)图片的坐标系
    public PointF display2Logic(float x, float y) {
        return new PointF(display2logicX(x), display2logicY(y));
    }

    public PointF display2Logic(PointF pointF) {
        return new PointF(display2logicX(pointF.x), display2logicY(pointF.y));
    }

    public PointF display2Origin(float x, float y) {
        float originX = x - centerTranX;
        float originY = y - centerTranY;
        if (originX < 0) originX = 0;
        if (originX > bitmapWidth) originX = bitmapWidth;
        if (originY < 0) originY = 0;
        if (originY > bitmapHeight) originY = bitmapHeight;
        return new PointF(originX, originY);
    }

    //将设备坐标系内x点转化为逻辑坐标系内的y点,并发给远端,
    // 相对于图片内部的坐标
    public float display2logicX(float x) {
        if (x < centerTranX) {
            x = centerTranX;
        }
        if (x > centerTranX + bitmapWidth) {
            x = centerTranX + bitmapWidth;
        }
        return (x - centerTranX) * 100 / bitmapWidth;
    }

    //将设备坐标系内y点转化为逻辑坐标系内的y点,并发给远端
    public float display2logicY(float y) {
        if (y < centerTranY) {
            y = centerTranY;
        }
        if (y > centerTranY + bitmapHeight) {
            y = centerTranY + bitmapHeight;
        }
        return (y - centerTranY) * 100 / bitmapHeight;
    }


    //远程的点转化为逻辑坐标系内的点
    public PointF origin2Logic(PointF originP) {
        return new PointF(originP.x / bitmapWidth * 100, originP.y / bitmapHeight * 100);
    }

    public float logic2DisplayDx(float x) {
        return x / 100 * bitmapWidth;
    }

    public float logic2DisplayDy(float y) {
        return y / 100 * bitmapHeight;
    }
}
