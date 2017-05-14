package com.demo.drawpaintview.paint.bean;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by xu.wang
 * Date on 2017/test2/15 16:50
 */
public class PointInfo implements Parcelable {
    private PointF startPointF;   //开始点的坐标
    private PointF crtlPointF;      //当前控制点的坐标
    private int index;          //当前是第几个点

    public PointInfo(PointF startPointF, PointF crtlPointF, int index) {
        this.startPointF = startPointF;
        this.crtlPointF = crtlPointF;
        this.index = index;
    }

    protected PointInfo(Parcel in) {
        startPointF = in.readParcelable(PointF.class.getClassLoader());
        crtlPointF = in.readParcelable(PointF.class.getClassLoader());
        index = in.readInt();
    }

    public static final Creator<PointInfo> CREATOR = new Creator<PointInfo>() {
        @Override
        public PointInfo createFromParcel(Parcel in) {
            return new PointInfo(in);
        }

        @Override
        public PointInfo[] newArray(int size) {
            return new PointInfo[size];
        }
    };

    public PointF getStartPointF() {
        return startPointF;
    }

    public void setStartPointF(PointF startPointF) {
        this.startPointF = startPointF;
    }

    public PointF getCrtlPointF() {
        return crtlPointF;
    }

    public void setCrtlPointF(PointF crtlPointF) {
        this.crtlPointF = crtlPointF;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(startPointF, i);
        parcel.writeParcelable(crtlPointF, i);
        parcel.writeInt(index);
    }
}
