package com.demo.drawpaintview.paint.interf;

import com.demo.drawpaintview.paint.bean.shape.LineInfo;
import com.demo.drawpaintview.paint.util.TransPointF;

/**
 * Created by xu.wang
 * Date on 2017/8/30 14:24
 * PaintAttacher逻辑和View之间的回调
 */

public interface OnDrawListener {
    void drawLine(LineInfo lineInfo, TransPointF transPointF);  //刷新画某一条线条,在批注状态下,为了提高批注速度,绘制CanvasBitmap和最后一笔

    void refreshCanvasBitmap();     //让画板销毁canvasBitmap,并在canvas上绘制当前所有线条,用于解决内存消耗过多的问题...
}
