package com.demo.drawpaintview.view;

import android.graphics.PointF;

import java.io.Serializable;

/**
 * Created by xu.wang
 * Date on 2017/2/15 16:50
 */
public class PointInfo implements Serializable {
    private PointF prePointF;   //上一个点的坐标
    private PointF pointF;      //当前点的坐标
    private int index;          //当前是第几个点

    public PointInfo(PointF prePointF, PointF pointF,int index) {
        this.prePointF = prePointF;
        this.pointF = pointF;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public PointF getPrePointF() {
        return prePointF;
    }

    public void setPrePointF(PointF prePointF) {
        this.prePointF = prePointF;
    }

    public PointF getPointF() {
        return pointF;
    }

    public void setPointF(PointF pointF) {
        this.pointF = pointF;
    }

}
