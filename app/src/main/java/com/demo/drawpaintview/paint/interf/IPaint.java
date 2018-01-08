package com.demo.drawpaintview.paint.interf;

import android.graphics.Bitmap;

import com.demo.drawpaintview.paint.bean.ActionLineInfo;
import com.demo.drawpaintview.paint.bean.PageInfo;
import com.demo.drawpaintview.paint.bean.shape.LineInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xu.wang
 * Date on 2017/7/18 11:52
 * 自定义绘制模块(PaintBlackBoardView,PaintImageView)共有的功能
 */

public interface IPaint {
    void setDrawInfo(PageInfo pageInfo);     //设置当前页消息,绘制所有线条

    void setStrokeWidth(int strokeWidth);  //设置线条粗细

    void undo();            //撤销 ,返回true表示,接下来可以继续撤销,false,下一步没有可以撤销的内容

    void redo();            //恢复,同上,可用与优化用户体验...

    void clearLocalData();           //清除所有画笔数据,不会将该动作记录进栈,也不会发送

    void clear();           //清除画板,不会删除线条,而是不在画板上画线,会将动作记录进栈,并且同步向发送列表发送

    void setCurrentColor(int color); //设置当前的颜色


    ArrayList<LineInfo> getDrawInfo();      //获取本页批注信息,不包括 undo , redo信息,
    // 因为PageInfo中记录的一些信息,自定义View中没有必要获取,也没有获得, 所以建议配合getUndoInfo(),getRedoInfo(),自行去生成每页的PageInfo的对象

    ArrayList<ActionLineInfo> getUndoInfo();                 //获取本页 undo 信息

    ArrayList<ActionLineInfo> getRedoInfo();                 //获取本页 redo 信息

    Bitmap getBitmap();                     //获得本页bitmap,用于截图...
}
