package com.demo.drawpaintview.view;

import android.graphics.Paint;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xu.wang
 * Date on 2017/1/19 15:00
 * 绘画的每一条线条的对象
 */
public class LineInfo implements Serializable {
    private String lineId;  //线条的id
    private Paint paint = new Paint();    //画笔的paint
    private ArrayList<PointInfo> currentPointLists = new ArrayList<>();

    public LineInfo() {
    }

    public LineInfo(String lineId, Paint paint, ArrayList<PointInfo> pointLists) {
        this.lineId = lineId;
        this.paint = paint;
        this.currentPointLists = pointLists;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public ArrayList<PointInfo> getCurrentPointLists() {
        return currentPointLists;
    }

    public void setCurrentPointLists(ArrayList<PointInfo> mCurrentPointLists) {
        this.currentPointLists = mCurrentPointLists;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
