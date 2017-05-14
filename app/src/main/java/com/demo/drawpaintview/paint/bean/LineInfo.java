package com.demo.drawpaintview.paint.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by xu.wang
 * Date on 2017/test1/19 15:00
 * 绘画的每一条直线的对象
 */
public class LineInfo implements Parcelable {
    private String lineId;  //直线的id
    private int color;
    private int strokeWidth;
    //0,贝塞尔曲线, test1,荧光笔,test2, 矩形 , 3 椭圆, 4,箭头
    private int type = 0;
    public ArrayList<PointInfo> currentPointLists = new ArrayList<>();

    public LineInfo() {
    }

    public LineInfo(String lineId, int color,int strokeWidth, ArrayList<PointInfo> pointLists) {
        this.lineId = lineId;
        this.strokeWidth = strokeWidth;
        this.color = color;
        this.currentPointLists = pointLists;
    }

    protected LineInfo(Parcel in) {
        lineId = in.readString();
        color = in.readInt();
        strokeWidth = in.readInt();
        currentPointLists = in.createTypedArrayList(PointInfo.CREATOR);
        type = in.readInt();
    }

    public static final Creator<LineInfo> CREATOR = new Creator<LineInfo>() {
        @Override
        public LineInfo createFromParcel(Parcel in) {
            return new LineInfo(in);
        }

        @Override
        public LineInfo[] newArray(int size) {
            return new LineInfo[size];
        }
    };

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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static Creator<LineInfo> getCREATOR() {
        return CREATOR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(lineId);
        parcel.writeInt(color);
        parcel.writeInt(strokeWidth);
        parcel.writeTypedList(currentPointLists);
        parcel.writeInt(type);
    }
}
