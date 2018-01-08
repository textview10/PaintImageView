package com.demo.drawpaintview.paint.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.demo.drawpaintview.paint.bean.shape.LineInfo;

import java.util.ArrayList;

/**
 * Created by xu.wang
 * Date on 2017/9/4 11:54
 * 负责记录当前一笔的undo ,redo 信息
 */

public class ActionLineInfo implements Parcelable {
    public ActionLineInfo(int cmdType, Object obj) {
        this.cmdType = cmdType;
        this.obj = obj;
    }

    private int cmdType;    //当前命令的执行类型
    private Object obj;     //当前命令记录的线条信息

    protected ActionLineInfo(Parcel in) {
        cmdType = in.readInt();
        if (cmdType == 1 || cmdType == 2) { //1,2分别是删除和恢复单线条
            obj = in.readParcelable(LineInfo.class.getClassLoader());
        } else if (cmdType == 3 || cmdType == 4) { //3,4分别是删除和恢复多线条
            obj = in.createTypedArrayList(LineInfo.CREATOR);
        }
    }

    public static final Creator<ActionLineInfo> CREATOR = new Creator<ActionLineInfo>() {
        @Override
        public ActionLineInfo createFromParcel(Parcel in) {
            return new ActionLineInfo(in);
        }

        @Override
        public ActionLineInfo[] newArray(int size) {
            return new ActionLineInfo[size];
        }
    };

    public int getCmdType() {
        return cmdType;
    }

    public void setCmdType(int cmdType) {
        this.cmdType = cmdType;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cmdType);
        if (obj instanceof ArrayList) {
            dest.writeTypedList((ArrayList<LineInfo>) obj);
        } else if (obj instanceof LineInfo) {
            dest.writeParcelable((LineInfo) obj, flags);
        }

    }
}
