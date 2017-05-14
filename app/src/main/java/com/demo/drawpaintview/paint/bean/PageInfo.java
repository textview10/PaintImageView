package com.demo.drawpaintview.paint.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by xu.wang
 * Date on 2017/test2/21 14:43
 */
public class PageInfo implements Parcelable{
    private int index;      //第几张图片
    private ArrayList<LineInfo> lineInfos;  //线条信息
    private String path;                    //路径

    public PageInfo(int index, ArrayList<LineInfo> lineInfos, String path) {
        this.index = index;
        this.lineInfos = lineInfos;
        this.path = path;
    }

    protected PageInfo(Parcel in) {
        index = in.readInt();
        lineInfos = in.createTypedArrayList(LineInfo.CREATOR);
        path = in.readString();
    }

    public static final Creator<PageInfo> CREATOR = new Creator<PageInfo>() {
        @Override
        public PageInfo createFromParcel(Parcel in) {
            return new PageInfo(in);
        }

        @Override
        public PageInfo[] newArray(int size) {
            return new PageInfo[size];
        }
    };

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ArrayList<LineInfo> getLineInfos() {
        return lineInfos;
    }

    public void setLineInfos(ArrayList<LineInfo> lineInfos) {
        this.lineInfos = lineInfos;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(index);
        parcel.writeTypedList(lineInfos);
        parcel.writeString(path);
    }
}
