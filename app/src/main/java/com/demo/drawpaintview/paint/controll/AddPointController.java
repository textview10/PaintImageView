package com.demo.drawpaintview.paint.controll;

import android.graphics.PointF;
import com.demo.drawpaintview.paint.PaintAttacher;
import com.demo.drawpaintview.paint.PaintConfig;
import com.demo.drawpaintview.paint.bean.shape.LineInfo;
import com.demo.drawpaintview.paint.util.Build32Code;
import com.demo.drawpaintview.paint.util.ColorUtil;
import com.demo.drawpaintview.paint.util.PaintMathUtils;
import com.demo.drawpaintview.paint.util.TransPointF;
/**
 * Created by xu.wang
 * Date on  2017/11/8 17:40:19.
 *
 * @Desc 根据触摸事件, 按逻辑增加点到list的控制类
 */

public class AddPointController {
    private final String TAG = "AddPointController";
    private int currentColor; //记录画笔颜色
    private int currentPaintSize; //记录画笔粗细
    private PaintAttacher mAttacher;
    private int currentPoint = 1;   //当前是本次绘制的第几个点

    public AddPointController(PaintAttacher attacher, int currentColor, int currentPaintSize) {
        this.mAttacher = attacher;
        this.currentColor = currentColor;
        this.currentPaintSize = currentPaintSize;
    }

    /**
     * 贝塞尔模式下储存的数据结构:
     * ---------  第1个点-------   第2个点-------        第三个点   -----向后以次类推
     * startP:      p0             p0,               (p0 + p1) /2
     * ctrlP:       p0             p0                     p1
     * endP:        p0          (p0 + p1) /2         (p1 + p2) / 2
     *
     * @param lineInfo
     * @param transPointF
     * @param downX
     * @param downY
     */
    public void actionDown(LineInfo lineInfo, TransPointF transPointF, float downX, float downY) {
        lineInfo.setLineId(Build32Code.createGUID());
        if (mAttacher.getDrawState() == PaintAttacher.DrawState.BEZIER || mAttacher.getDrawState() == PaintAttacher.DrawState.PEN_YINGGUANG) {
            if (mAttacher.getDrawState() == PaintAttacher.DrawState.BEZIER) {
                lineInfo.setColor(currentColor);
                lineInfo.setStrokeWidth(currentPaintSize);
                if (lineInfo.getPointLists().size() != 0) { //如果是第一个点的话,三个点都是原点
//                    Log.writeLog(TAG, "actionDown the pointList.size != 0");
                }
                PointF tempStartP;
                PointF tempCtrlP;
                PointF tempEndP;
                tempEndP = tempCtrlP = tempStartP = transPointF.display2Logic(downX, downY);
                lineInfo.getPointLists().add(tempStartP);  //startPoint,起始点
                lineInfo.getPointLists().add(tempCtrlP);  //ctrlPoint,控制点
                lineInfo.getPointLists().add(tempEndP);  //endPoint,结束点

            } else if (mAttacher.getDrawState() == PaintAttacher.DrawState.PEN_YINGGUANG) {
                int currentAlphaColor = ColorUtil.convertColorToString(currentColor, PaintConfig.mYingGuangAlhpa);
                lineInfo.setColor(currentAlphaColor);
                lineInfo.setStrokeWidth(PaintConfig.mYingGuangSize);
                PointF tempStartP;
                PointF tempCtrlP;
                PointF tempEndP;
                tempEndP = tempCtrlP = tempStartP = transPointF.display2Logic(downX, downY);
                lineInfo.getPointLists().add(tempStartP);  //startPoint,起始点
                lineInfo.getPointLists().add(tempCtrlP);  //ctrlPoint,控制点
                lineInfo.getPointLists().add(tempEndP);  //endPoint,结束点

            }

        } else if (mAttacher.getDrawState() == PaintAttacher.DrawState.ARROW || mAttacher.getDrawState() == PaintAttacher.DrawState.ELLIPSE || mAttacher.getDrawState() == PaintAttacher.DrawState.RECTANGLE
                || mAttacher.getDrawState() == PaintAttacher.DrawState.TIANZHIGE || mAttacher.getDrawState() == PaintAttacher.DrawState.MIZIGE || mAttacher.getDrawState() == PaintAttacher.DrawState.SIXIANGE) {
            lineInfo.setColor(currentColor);
            lineInfo.setStrokeWidth(currentPaintSize);
            lineInfo.getPointLists().add(transPointF.display2Logic(downX, downY));
            switch (mAttacher.getDrawState()) {
                case ARROW:
                    lineInfo.setType(4);
                    break;
                case ELLIPSE:
                    lineInfo.setType(3);
                    break;
                case RECTANGLE:
                    lineInfo.setType(2);
                    break;
                case TIANZHIGE:
                    lineInfo.setType(5);
                    break;
                case MIZIGE:
                    lineInfo.setType(6);
                    break;
                case SIXIANGE:
                    lineInfo.setType(7);
                    break;
            }
        } else if (mAttacher.getDrawState() == PaintAttacher.DrawState.POLYLINE) {
            lineInfo.setType(8);
            lineInfo.setColor(currentColor);
            lineInfo.setStrokeWidth(currentPaintSize);
        }
    }

    public void actionMove(LineInfo lineInfo, TransPointF transPointF, float preX, float preY, float moveX, float moveY) {
        if (lineInfo == null) return;
        if (mAttacher.getDrawState() == PaintAttacher.DrawState.BEZIER || mAttacher.getDrawState() == PaintAttacher.DrawState.PEN_YINGGUANG) {
            PointF tempStartP;
            PointF tempCtrlP;
            PointF tempEndP;
            if (lineInfo.getPointLists().size() == 0) { //如果是第一个点的话,三个点都是原点
                tempEndP = tempCtrlP = tempStartP = transPointF.display2Logic(moveX, moveY);
            } else if (lineInfo.getPointLists().size() == 3) {
                tempCtrlP = tempStartP = lineInfo.getPointLists().get(lineInfo.getPointLists().size() - 1);
                tempEndP = PaintMathUtils.getBesPoint(tempCtrlP, transPointF.display2Logic(moveX, moveY));
            } else {
                tempStartP = lineInfo.getPointLists().get(lineInfo.getPointLists().size() - 1);
                tempCtrlP = transPointF.display2Logic(preX, preY);
                tempEndP = PaintMathUtils.getBesPoint(tempCtrlP, transPointF.display2Logic(moveX, moveY));
            }
            lineInfo.getPointLists().add(tempStartP);  //startPoint,起始点
            lineInfo.getPointLists().add(tempCtrlP);  //ctrlPoint,控制点
            lineInfo.getPointLists().add(tempEndP);  //endPoint,结束点


        } else if (mAttacher.getDrawState() == PaintAttacher.DrawState.RECTANGLE || mAttacher.getDrawState() == PaintAttacher.DrawState.ARROW || mAttacher.getDrawState() == PaintAttacher.DrawState.ELLIPSE
                || mAttacher.getDrawState() == PaintAttacher.DrawState.TIANZHIGE || mAttacher.getDrawState() == PaintAttacher.DrawState.MIZIGE || mAttacher.getDrawState() == PaintAttacher.DrawState.SIXIANGE) {
            if (lineInfo.getPointLists().size() > 1) {
                lineInfo.getPointLists().set(1, mAttacher.getTransPointF().display2Logic(moveX, moveY));
            } else {
                lineInfo.getPointLists().add(mAttacher.getTransPointF().display2Logic(moveX, moveY));
            }
        } else if (mAttacher.getDrawState() == PaintAttacher.DrawState.POLYLINE) {
            lineInfo.getPointLists().add(mAttacher.getTransPointF().display2Logic(moveX, moveY));
        }
        currentPoint++;
    }

    public void actionUp(LineInfo lineInfo, float upX, float upY) {

    }

    public void setColor(int color) {
        this.currentColor = color;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.currentPaintSize = strokeWidth;
    }


    public int getColor() {
        return currentColor;
    }
}
