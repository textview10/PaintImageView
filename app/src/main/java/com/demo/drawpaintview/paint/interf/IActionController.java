package com.demo.drawpaintview.paint.interf;

import com.demo.drawpaintview.paint.bean.ActionLineInfo;
import com.demo.drawpaintview.paint.bean.shape.LineInfo;

import java.util.ArrayList;

/**
 * Created by xu.wang
 * Date on 2017/9/1 17:33
 * undo ,redo Stack的接口
 */

public interface IActionController {
    void undo(ArrayList<LineInfo> lists);    //撤销

    void redo(ArrayList<LineInfo> lists);    //恢复

    void receiveUndoRedo(ArrayList<LineInfo> lists, String receiveBody);          //根据远端指令删除的线条,不在本地stack中保存记录

    void pushData(LineInfo lineInfo, int type);          //增加单个线条及当时相应命令模式

    void pushData(ArrayList<LineInfo> lists, int type);  //增加多个线条及当时相应命令模式

    ArrayList<ActionLineInfo> getUndoList();            //获得全部undo记录

    ArrayList<ActionLineInfo> getRedoList();            //获得全部恢复记录

    void setUndoList(ArrayList<ActionLineInfo> undoList);                   //设置undo的历史信息

    void setRedoList(ArrayList<ActionLineInfo> redoList);                  //设置redo的历史信息
}
