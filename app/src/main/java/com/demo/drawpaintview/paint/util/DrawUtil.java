package com.demo.drawpaintview.paint.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.demo.drawpaintview.paint.bean.shape.LineInfo;

import java.util.ArrayList;

/**
 * Created by xu.wang
 * Date on 2017/2/20 13:15
 * 绘制各种图形的类
 */
public class DrawUtil {

    /**
     * 向画板上画全部记录的全部点的轨迹,绘制过程比较慢...
     *
     * @param canvas      画布
     * @param list        所有记录信息集合
     * @param transPointF 映射坐标的操作类
     */

    public static void drawAllPath(Canvas canvas, ArrayList<LineInfo> list, TransPointF transPointF) {
        if (list == null) return;
        for (int i = 0; i < list.size(); i++) {
            LineInfo lineInfo = list.get(i);
            if (lineInfo.getIsDelete() != 0) { //该笔已删除
                continue;
            }
            drawPath(canvas, lineInfo, transPointF);
        }
    }


    /**
     * 绘制某一笔贝斯尔曲线
     *
     * @param canvas      画布
     * @param paint       画笔
     * @param lineInfo    点信息的集合
     * @param transPointF 映射坐标的工具类
     */
    public static void drawBesier(Canvas canvas, Paint paint, LineInfo lineInfo, TransPointF transPointF) {
        canvas.drawPath(PathFactory.createBezier(lineInfo, transPointF), paint);
    }

    /**
     * 绘制十字格
     *
     * @param canvas
     * @param startF
     * @param endF
     * @param mPaint
     */
    public static void drawShiZiGe(Canvas canvas, PointF startF, PointF endF, Paint mPaint) {
        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        RectF rectF = PathFactory.createRectF(startF, endF);
        canvas.drawRect(rectF, mPaint);
        mPaint.setPathEffect(effects);
        canvas.drawPath(PathFactory.createShiZi(rectF), mPaint);
    }

    /**
     * 绘制米字格
     *
     * @param canvas
     * @param startF
     * @param endF
     * @param mPaint
     */
    public static void drawMiZhiGe(Canvas canvas, PointF startF, PointF endF, Paint mPaint) {
        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        RectF rectF = PathFactory.createRectF(startF, endF);
        canvas.drawRect(rectF, mPaint);
        mPaint.setPathEffect(effects);
        canvas.drawPath(PathFactory.createMiZi(rectF), mPaint);
    }


    public static void drawSiXianGe(Canvas canvas, PointF startF, PointF endF, Paint mPaint) {
        float startX, startY, endX, endY = 0;
        if (endF.x > startF.x) {
            if (endF.y > startF.y) {
                startX = startF.x;
                startY = startF.y;
                endX = endF.x;
                endY = endF.y;
            } else {
                startX = startF.x;
                startY = endF.y;
                endX = endF.x;
                endY = startF.y;
            }
        } else {
            if (endF.y > startF.y) {
                startX = endF.x;
                startY = startF.y;
                endX = startF.x;
                endY = endF.y;
            } else {
                startX = endF.x;
                startY = endF.y;
                endX = startF.x;
                endY = startF.y;
            }
        }
        float disVer = (endY - startY) / 3;
        Path path = new Path();
        path.moveTo(startX, startY);
        path.lineTo(endX, startY);
        path.moveTo(startX, startY + disVer);
        path.lineTo(endX, startY + disVer);
        path.moveTo(startX, startY + disVer * 2);
        path.lineTo(endX, startY + disVer * 2);
        path.moveTo(startX, endY);
        path.lineTo(endX, endY);
        canvas.drawPath(path, mPaint);
    }

    /**
     * 绘制集合中的某一笔
     *
     * @param canvas      画板
     * @param lineInfo    包含一笔信息的对象
     * @param transPointF 映射坐标的操作类
     */
    public static void drawPath(Canvas canvas, LineInfo lineInfo, TransPointF transPointF) {
        if (lineInfo == null) {
            Log.e("DrawUtil", "当前一笔为空");
            return;
        }
        ArrayList<PointF> pointInfos = lineInfo.getPointLists();
        if (pointInfos.size() < 2) {
            return;
        }
        Paint paint = new Paint();
        paint.setColor(lineInfo.getColor());
        paint.setStrokeWidth(lineInfo.getStrokeWidth());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        switch (lineInfo.getType()) {
            case 0:
            case 1:
                drawBesier(canvas, paint, lineInfo, transPointF);
                break;
            case 2:
                drawRect(canvas, transPointF.logic2Display(pointInfos.get(0)),
                        transPointF.logic2Display(pointInfos.get(1)), paint);
                break;
            case 3:
                drawOval(canvas, transPointF.logic2Display(pointInfos.get(0)),
                        transPointF.logic2Display(pointInfos.get(1)), paint);
                break;
            case 4:
                drawArrow(canvas, transPointF.logic2Display(pointInfos.get(0)),
                        transPointF.logic2Display(pointInfos.get(1)), paint);
                break;
            case 5:
                drawShiZiGe(canvas, transPointF.logic2Display(pointInfos.get(0)),
                        transPointF.logic2Display(pointInfos.get(1)), paint);
                break;
            case 6:
                drawMiZhiGe(canvas, transPointF.logic2Display(pointInfos.get(0)),
                        transPointF.logic2Display(pointInfos.get(1)), paint);
                break;
            case 7:
                drawSiXianGe(canvas, transPointF.logic2Display(pointInfos.get(0)),
                        transPointF.logic2Display(pointInfos.get(1)), paint);
                break;
            case 8:
                drawPolyLine(canvas, lineInfo, paint, transPointF);
                break;
        }
    }

    /**
     * 绘制折线
     *
     * @param canvas
     * @param lineInfo
     * @param paint
     * @param transPointF
     */
    private static void drawPolyLine(Canvas canvas, LineInfo lineInfo, Paint paint, TransPointF transPointF) {
        Path path = PathFactory.createPolyLine(lineInfo, transPointF);
        canvas.drawPath(path, paint);
    }

    /**
     * @param canvas 画板
     * @param startP 开始点
     * @param endP   结束点
     * @param paint  画笔
     */
    public static void drawArrow(Canvas canvas, PointF startP, PointF endP, Paint paint) {
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawPath(PathFactory.createArrow(startP, endP), paint);
    }

    /**
     * 绘制正方形
     *
     * @param canvas 画布
     * @param startF 开始点
     * @param endF   结束点
     * @param paint  画笔
     */
    public static void drawRect(Canvas canvas, PointF startF, PointF endF, Paint paint) {
        canvas.drawRect(PathFactory.createRectF(startF, endF), paint);
    }

    public static void drawLine(Canvas canvas, PointF startF, PointF endF, Paint paint) {
        canvas.drawLine(startF.x, startF.y, endF.x, endF.y, paint);
        canvas.drawPoint(endF.x, endF.y, paint);
    }

    /**
     * 画圆的方法
     *
     * @param canvas 画布
     * @param startF 开始点
     * @param endF   结束点
     */
    public static void drawOval(Canvas canvas, PointF startF, PointF endF, Paint paint) {
        canvas.drawOval(PathFactory.createRectF(startF, endF), paint);
    }

    public static void drawArc(Canvas canvas, PointF startF, PointF endF, Paint paint) {

        Paint mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(30);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);

        Paint pPaint = new Paint();
        pPaint.setColor(Color.YELLOW);
        pPaint.setStrokeWidth(4);
        pPaint.setStyle(Paint.Style.STROKE);
        pPaint.setStrokeCap(Paint.Cap.ROUND);
        pPaint.setAntiAlias(true);

        Paint ppPaint = new Paint();
        ppPaint.setColor(Color.BLACK);
        ppPaint.setStrokeWidth(4);
        ppPaint.setStyle(Paint.Style.STROKE);
        ppPaint.setStrokeCap(Paint.Cap.ROUND);
        ppPaint.setAntiAlias(true);


        float startX, startY, endX, endY = 0;
        RectF rectF;
        float width = Math.abs(endF.x - startF.x) * 2;
        float height = Math.abs(endF.y - startF.y) * 2;
        if (startF.x < endF.x) {
            if (startF.y < endF.y) {
                startX = startF.x - width / 2;
                startY = startF.y;
                endX = endF.x;
                endY = endF.y + height / 2;

                rectF = new RectF(startX, startY, endX, endY);
                canvas.drawRect(rectF, mPaint);
                canvas.drawArc(rectF, 270, 90, false, paint);
                canvas.drawArc(rectF, 270, 90, false, pPaint);

            } else {
                startX = startF.x;
                startY = endF.y;
                endX = endF.x + width / 2;
                endY = startF.y + height / 2;


                rectF = new RectF(startX, startY, endX, endY);
                canvas.drawRect(rectF, mPaint);
                canvas.drawArc(rectF, 180, 90, false, paint);
                canvas.drawArc(rectF, 180, 90, false, pPaint);
            }
        } else {
            if (startF.y < endF.y) {
                startX = endF.x - width / 2;
                startY = startF.y - height / 2;
                endX = startF.x;
                endY = endF.y;

                rectF = new RectF(startX, startY, endX, endY);
                canvas.drawRect(rectF, mPaint);
                canvas.drawArc(rectF, 0, 90, false, paint);
                canvas.drawArc(rectF, 0, 90, false, pPaint);
            } else {
                startX = endF.x;
                startY = endF.y - height / 2;
                endX = startF.x + width / 2;
                endY = startF.y;

                rectF = new RectF(startX, startY, endX, endY);
                canvas.drawRect(rectF, mPaint);
                canvas.drawArc(rectF, 90, 90, false, paint);
                canvas.drawArc(rectF, 90, 90, false, pPaint);
            }
        }

        canvas.drawPoint(startF.x, startF.y, ppPaint);
        canvas.drawPoint(endF.x, endF.y, ppPaint);


    }
}
