package com.demo.drawpaintview.paint.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.demo.drawpaintview.paint.bean.shape.LineInfo;

import java.util.ArrayList;

/**
 * Created by xu.wang
 * Date on 2017/2/21 14:43
 */
public class PageInfo implements Parcelable {
    private int index;      //第几张图片
    private ArrayList<LineInfo> lineInfos;  //线条信息
    private String path;                    //路径
    private String id;                      //当前页的id;
    private ArrayList<ActionLineInfo> undoLists;    //记录撤销记录的集合
    private ArrayList<ActionLineInfo> redoLists;    //记录恢复记录的集合

    public PageInfo(int index, ArrayList<LineInfo> lineInfos, String path) {
        this.index = index;
        this.lineInfos = lineInfos;
        this.path = path;
        this.undoLists = new ArrayList<>();
        this.redoLists = new ArrayList<>();
    }

    public PageInfo(int index, ArrayList<LineInfo> lineInfos, String path, String id) {
        this.index = index;
        this.lineInfos = lineInfos;
        this.path = path;
        this.id = id;
        this.undoLists = new ArrayList<>();
        this.redoLists = new ArrayList<>();
    }


    public PageInfo(int index, ArrayList<LineInfo> lineInfos, String path, ArrayList<ActionLineInfo> undoLists, ArrayList<ActionLineInfo> redoLists) {
        this.index = index;
        this.lineInfos = lineInfos;
        this.path = path;
        this.undoLists = undoLists;
        this.redoLists = redoLists;
    }

    public PageInfo(int index, ArrayList<LineInfo> lineInfos, String path, String id, ArrayList<ActionLineInfo> undoLists, ArrayList<ActionLineInfo> redoLists) {
        this.index = index;
        this.lineInfos = lineInfos;
        this.path = path;
        this.id = id;
        this.undoLists = undoLists;
        this.redoLists = redoLists;
    }

    protected PageInfo(Parcel in) {
        index = in.readInt();
        lineInfos = in.createTypedArrayList(LineInfo.CREATOR);
        path = in.readString();
        id = in.readString();
        undoLists = in.createTypedArrayList(ActionLineInfo.CREATOR);
        redoLists = in.createTypedArrayList(ActionLineInfo.CREATOR);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<ActionLineInfo> getUndoLists() {
        return undoLists;
    }

    public void setUndoLists(ArrayList<ActionLineInfo> undoLists) {
        this.undoLists = undoLists;
    }

    public ArrayList<ActionLineInfo> getRedoLists() {
        return redoLists;
    }

    public void setRedoLists(ArrayList<ActionLineInfo> redoLists) {
        this.redoLists = redoLists;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(index);
        dest.writeTypedList(lineInfos);
        dest.writeString(path);
        dest.writeString(id);
        dest.writeTypedList(undoLists);
        dest.writeTypedList(redoLists);
    }
}
