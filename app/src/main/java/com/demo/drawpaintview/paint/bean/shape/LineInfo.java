package com.demo.drawpaintview.paint.bean.shape;

import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by xu.wang
 * Date on 2017/1/19 15:00
 * 绘画的每一条直线的对象
 */
public class LineInfo implements Parcelable {
    private String lineId;  //直线的id
    private int color;
    private int strokeWidth;
    private int isDelete = 0;     //是否将线条绘制到canvas上,1为已删除,不绘制,0未删除,绘制
    //0,贝塞尔曲线, 1,荧光笔,2, 矩形 , 3 椭圆, 4,箭头 5,田字格,6米字格,7,四线格8,直线
    private int type = 0;
    public ArrayList<PointF> pointLists = new ArrayList<>();

    public LineInfo(String lineId, int color, int strokeWidth, int type, ArrayList<PointF> pointLists) {
        this.lineId = lineId;
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.type = type;
        this.pointLists = pointLists;
    }

    public LineInfo() {
    }

    protected LineInfo(Parcel in) {
        lineId = in.readString();
        color = in.readInt();
        strokeWidth = in.readInt();
        isDelete = in.readInt();
        type = in.readInt();
        pointLists = in.createTypedArrayList(PointF.CREATOR);
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

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public ArrayList<PointF> getPointLists() {
        return pointLists;
    }

    public void setPointLists(ArrayList<PointF> pointLists) {
        this.pointLists = pointLists;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(lineId);
        dest.writeInt(color);
        dest.writeInt(strokeWidth);
        dest.writeInt(isDelete);
        dest.writeInt(type);
        dest.writeTypedList(pointLists);
    }

}
