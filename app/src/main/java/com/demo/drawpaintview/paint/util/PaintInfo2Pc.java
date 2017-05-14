package com.demo.drawpaintview.paint.util;



/**
 * Created by xu.wang
 * Date on 2017/test1/19 14:02
 * 向pc端发送点和线信息的类
 */
public class PaintInfo2Pc {
    private boolean isSend = true;  //是否将点发送到pc端,默认发送

    public PaintInfo2Pc() {
    }

    //是否发送小心到pc端
    public void setIsSend(boolean isSend) {
        this.isSend = isSend;
    }

    public void sendDownInfo(String lineId, String type, int currntColor, int currentPaintSize, float moveX, float moveY) {
        if (!isSend) {
            return;
        }

    }

    /**
     * 手势移动时发送绘制点的方法
     * @param lineId       绘制线条的id
     * @param currentPoint 当前线条的第几个点
     * @param startX       开始点x坐标
     * @param startY       开始点y坐标
     * @param controllX    控制点x坐标
     * @param controllY    控制点y坐标
     */
    public void sendMoveInfo(String lineId, int currentPoint, float startX, float startY, float controllX, float controllY) {
        if (!isSend) {
            return;
        }

    }

    public void setMoveShape(String lineId, int currentPoint, float mX, float mY) {
        if (!isSend) {
            return;
        }

    }

    /**
     * 清除画板
     */
    public void sendMoveAllPath() {
        if (!isSend) {
            return;
        }
    }

    public void sendUndoInfo(String shapeId) {
        if (!isSend) {
            return;
        }
    }

    public void sendRedoInfo(String shapeId) {
        if (!isSend) {
            return;
        }
    }

    public void resetCordinate(int width, int height) {
        if (!isSend) {
            return;
        }
    }

    public void sendTransLateInfo(float disX, float disY) {
        if (!isSend) {
            return;
        }
    }

    public void sendScaleInfo(float scale, float focusX, float focusY) {
        if (!isSend) {
            return;
        }
    }

    public void sendRotateInfo(int rotate) {
        if (!isSend) {
            return;
        }
    }

    public void clearMatrix() {
        if (!isSend) {
            return;
        }
    }

}
