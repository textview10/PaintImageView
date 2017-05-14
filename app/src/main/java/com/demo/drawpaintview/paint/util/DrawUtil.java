package com.demo.drawpaintview.paint.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;

import com.demo.drawpaintview.paint.TransPointF;
import com.demo.drawpaintview.paint.bean.LineInfo;
import com.demo.drawpaintview.paint.bean.PointInfo;

import java.util.ArrayList;

/**
 * modify on 2017/5/14
 */
public class DrawUtil {
    private static float arrowAngle = (float) (40 * 3.14 / 360);
    private static final int arrowLength = 30;

    /**
     * @param canvas 画板
     * @param startP 开始点
     * @param endP   结束点
     * @param paint  画笔
     * @author 孙潮宗 计算箭头的算法,复制自pc客户端
     */
    public static void drawArrow(Canvas canvas, PointF startP, PointF endP, Paint paint) {
        float lineAngle = (float) Math.atan((endP.y - startP.y) / (endP.x - startP.x));
        float angleA1 = lineAngle - arrowAngle;
        float angleA2 = lineAngle + arrowAngle;
        int adjust = (endP.x >= startP.x) ? -1 : 1;
        float a1X = (float) (endP.x + adjust * arrowLength * Math.cos(angleA1));
        float a1Y = (float) (endP.y + adjust * arrowLength * Math.sin(angleA1));
        float a2X = (float) (endP.x + adjust * arrowLength * Math.cos(angleA2));
        float a2Y = (float) (endP.y + adjust * arrowLength * Math.sin(angleA2));
        PointF _arrowPoint1 = new PointF(a1X, a1Y);
        PointF _arrowPoint2 = new PointF(a1X + (a2X - a1X) / 3, a1Y + (a2Y - a1Y) / 3);
        PointF _arrowPoint3 = new PointF(a1X + (a2X - a1X) * 2 / 3, a1Y + (a2Y - a1Y) * 2 / 3);
        PointF _arrowPoint4 = new PointF(a2X, a2Y);
        double minPointDistance = arrowLength * Math.cos(arrowAngle);
        PointF[] points;
        if (distance(startP, endP) > minPointDistance) {
            points = new PointF[]{startP, _arrowPoint2, _arrowPoint1, endP, _arrowPoint4, _arrowPoint3, startP};
        } else {
            points = new PointF[]{_arrowPoint1, endP, _arrowPoint4, _arrowPoint1};
        }
        Path path = new Path();
        for (int i =0; i < points.length;i++){
            if (i == 0){
                path.moveTo(points[0].x,points[0].y);
            } else {
                path.lineTo(points[i].x,points[i].y);
            }
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawPath(path, paint);
    }

    private static double distance(PointF startP, PointF endP) {
        return Math.sqrt(Math.pow(Math.abs(startP.x - endP.x), 2) + Math.pow(Math.abs(startP.y - endP.y), 2));
    }

    public static void drawRect(Canvas canvas, PointF startF, PointF endF, Paint paint) {
        drawRect(canvas, startF.x, startF.y, endF.x, endF.y, paint);
    }

    public static void drawRect(Canvas canvas, float sx, float sy, float dx, float dy, Paint paint) {
        // 保证　左上角　与　右下角　的对应关系
        if (sx < dx) {
            if (sy < dy) {
                canvas.drawRect(sx, sy, dx, dy, paint);
            } else {
                canvas.drawRect(sx, dy, dx, sy, paint);
            }
        } else {
            if (sy < dy) {
                canvas.drawRect(dx, sy, sx, dy, paint);
            } else {
                canvas.drawRect(dx, dy, sx, sy, paint);
            }
        }
    }

    public static void drawOval(Canvas canvas, PointF startF, PointF endF, Paint mPaint) {
        drawOval(canvas, startF.x, startF.y, endF.x, endF.y, mPaint);
    }

    public static void drawOval(Canvas canvas, float moveX, float moveY, float downX, float downY, Paint mPaint) {
        canvas.drawOval(new RectF(moveX, moveY, downX, downY), mPaint);
    }

    public static String getTouchLineId(float x, float y, TransPointF transPointF, ArrayList<LineInfo> mPaintLines) {
        PointF pointF = transPointF.display2Logic(x, y);
        int area = 3;
        for (int i = 0; i < mPaintLines.size(); i++) {
            Path path = new Path();
            LineInfo lineInfo = mPaintLines.get(i);
            ArrayList<PointInfo> currentPointLists = lineInfo.getCurrentPointLists();
            if (currentPointLists.size() < 1) {
                return null;
            }
            for (int j = 0; j < currentPointLists.size(); j++) {
                PointInfo pointInfo = currentPointLists.get(j);
                if (j == 0) {
                    path.moveTo(pointInfo.getCrtlPointF().x, pointInfo.getCrtlPointF().y);
                } else {
                    path.quadTo(pointInfo.getStartPointF().x, pointInfo.getStartPointF().y,
                            pointInfo.getCrtlPointF().x, pointInfo.getCrtlPointF().y);
                }
            }
            RectF bounds = new RectF();
            path.computeBounds(bounds, true);
            Region region = new Region();
            region.setPath(path, new Region((int) bounds.left, (int) bounds.top, (int) bounds.right, (int) bounds.bottom));
            if (region.quickReject(new Rect((int)pointF.x - 2,(int)pointF.y- 2,(int)pointF.x + 2,(int) pointF.y+ 2))) {
                return lineInfo.getLineId();
            }
        }
        return null;
    }
}
