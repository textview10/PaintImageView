package com.demo.drawpaintview.paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.Toast;

import com.demo.drawpaintview.App;
import com.demo.drawpaintview.paint.bean.LineInfo;
import com.demo.drawpaintview.paint.bean.PageInfo;
import com.demo.drawpaintview.paint.bean.PointInfo;
import com.demo.drawpaintview.paint.util.DrawPathCong;
import com.demo.drawpaintview.paint.util.DrawUtil;
import com.demo.drawpaintview.paint.util.PaintInfo2Pc;
import com.demo.drawpaintview.util.Build32Code;
import com.demo.drawpaintview.util.ColorUtil;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by xu.wang
 * Date on 2017/test2/20 13:15
 * modify on 2017/5/14
 * 自定义图片批注控件
 */
public class PaintImageView extends ImageView implements ScaleGestureDetector.OnScaleGestureListener {
    private String TAG = getClass().getSimpleName().toString();
    private Context mContext;
    private Canvas mCanvas;         //单缓存缓存历史线条
    private Bitmap mCanvasBitmap; //用来绘制线条的bitmap
    private Paint mPaint;
    private ArrayList<LineInfo> mPaintLines;
    private PageInfo pageInfo;
    private ArrayList<LineInfo> mRedoPaintLines;
    private int currntColor; //记录画笔颜色
    private int currentPaintSize; //记录画笔粗细
    private ScaleGestureDetector mScaleGestureDetector = null;  //缩放检查手势的类
    private int currentPoint = 0;   //当前是本次绘制的第几个点
    private float mScale; // 缩放倍数, mScale
    private float mStartScale = 1.0f;
    private Matrix mMatrix = new Matrix();     //
    private float[] mMatrixValues = new float[9];
    private float preX, preY = 0;   //上一个点的位置
    private int mBitmapWidth, mBitmapHeight = -1;
    private float mCentreTranX, mCentreTranY;// 图片初始化居中时的偏移
    private PaintInfo2Pc paintInfo2Pc;      //向pc端发送消息的对象
    private int mTouchSlop;             //最小滑动距离
    private int mCurrentRotate = 0;
    private DrawState mDrawState = DrawState.NONE; //当前绘制状态
    private boolean isCanClick = false; //是否放大 true能, ,默认false
    private boolean isDraw = false; //是否在一次触摸事件之中.
    private TransPointF transPointF;
    private LineInfo lineInfo;

    public enum DrawState {
        //无状态,手写, 橡皮,荧光笔 ,带箭头的直线,正方形,圆形
        NONE, HANDWRITE, ERASER, PEN_YINGGUANG, ARROW, RECTANGLE, ELLIPSE
    }

    public PaintImageView(Context context) {
        this(context, null);
    }

    public PaintImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initialData();
    }

    private void initialData() {
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mScale = 1f;    //手势放大倍数初始值为1
        currntColor = DrawPathCong.mStartColor;
        currentPaintSize = DrawPathCong.mStartSize;
        paintInfo2Pc = new PaintInfo2Pc();
        mPaintLines = new ArrayList<>();
        mRedoPaintLines = new ArrayList<>();
        Collections.synchronizedList(mRedoPaintLines);
        Collections.synchronizedList(mPaintLines);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        initPaint();
        mScaleGestureDetector = new ScaleGestureDetector(mContext, this);
    }

    /**
     * 初始化画板,为简化逻辑,使用统一变量记录当前画笔颜色,粗细
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(currntColor);
//        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(currentPaintSize);
    }

    //初始化画布
    private void initDrawCanvas() {
        if (getWidth() > 0 && getHeight() > 0) {
            if (mCanvasBitmap != null) {
                mCanvasBitmap.recycle();
                mCanvasBitmap = null;
            }
            mCanvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Bitmap.Config.ARGB_4444);
            mCanvas = new Canvas(mCanvasBitmap);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMatrix != null) {
            canvas.setMatrix(mMatrix);
        }
        super.onDraw(canvas);
        if (mCanvasBitmap != null) {
            canvas.drawBitmap(mCanvasBitmap, 0, 0, mPaint);
        }
        if (mDrawState != DrawState.NONE && isDraw) {
            drawCurrentPointPath(canvas);
        }
    }

    //绘制集合中的最后一笔
    private void drawCurrentPointPath(Canvas canvas) {
        if (mPaintLines.size() < 1) {
            return;
        }
        LineInfo lineInfo = mPaintLines.get(mPaintLines.size() - 1);
        ArrayList<PointInfo> pointInfos = lineInfo.getCurrentPointLists();
        if (pointInfos.size() < 2) {
            return;
        }
        Paint paint = new Paint();
        paint.setColor(lineInfo.getColor());
        paint.setStrokeWidth(lineInfo.getStrokeWidth());
        paint.setStyle(Paint.Style.STROKE);
        switch (lineInfo.getType()) {
            case 0:
            case 1:
                drawCurrentPoint(canvas, paint, lineInfo);
                break;
            case 2:
                DrawUtil.drawRect(canvas, transPointF.logic2Display(pointInfos.get(0).getCrtlPointF()),
                        transPointF.logic2Display(pointInfos.get(1).getCrtlPointF()), paint);
                break;
            case 3:
                DrawUtil.drawOval(canvas, transPointF.logic2Display(pointInfos.get(0).getCrtlPointF()),
                        transPointF.logic2Display(pointInfos.get(1).getCrtlPointF()), paint);
                break;
            case 4:
                DrawUtil.drawArrow(canvas, transPointF.logic2Display(pointInfos.get(0).getCrtlPointF()),
                        transPointF.logic2Display(pointInfos.get(1).getCrtlPointF()), paint);
                break;
        }
    }

    //向画板上画全部记录的全部点的轨迹,绘制过程比较慢...
    private void drawAllPointPath(Canvas cavas) {
        for (int i = 0; i < mPaintLines.size(); i++) {
            Paint paint = new Paint();
            LineInfo lineInfo = mPaintLines.get(i);
            ArrayList<PointInfo> currentPointLists = lineInfo.getCurrentPointLists();
            if (currentPointLists.size() < 2) {
                return;
            }
            paint.setColor(lineInfo.getColor());
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(lineInfo.getStrokeWidth());
            switch (lineInfo.getType()) {
                case 0:
                case 1:
                    drawCurrentPoint(cavas, paint, lineInfo);
                    break;
                case 2:
                    DrawUtil.drawRect(cavas, transPointF.logic2Display(currentPointLists.get(0).getCrtlPointF()),
                            transPointF.logic2Display(currentPointLists.get(1).getCrtlPointF()), paint);
                    break;
                case 3:
                    DrawUtil.drawOval(cavas, transPointF.logic2Display(currentPointLists.get(0).getCrtlPointF()),
                            transPointF.logic2Display(currentPointLists.get(1).getCrtlPointF()), paint);
                    break;
                case 4:
                    DrawUtil.drawArrow(cavas, transPointF.logic2Display(currentPointLists.get(0).getCrtlPointF()),
                            transPointF.logic2Display(currentPointLists.get(1).getCrtlPointF()), paint);
                    break;
            }
        }
    }

    //绘制当前一笔直线
    private void drawCurrentPoint(Canvas canvas, Paint paint, LineInfo lineInfo) {
        Path path = new Path();
        ArrayList<PointInfo> pointInfos = lineInfo.getCurrentPointLists();
        for (int j = 0; j < pointInfos.size(); j++) {
            PointF pointF = transPointF.logic2Display(pointInfos.get(j).getCrtlPointF());
            if (j == 0) {
                path.moveTo(pointF.x, pointF.y);
            } else {
                PointF prePointF = transPointF.logic2Display(pointInfos.get(j).getStartPointF());
                path.quadTo(prePointF.x, prePointF.y, pointF.x, pointF.y);     //绘制贝斯尔曲线
            }
        }
        canvas.drawPath(path, paint);
    }

    // 根据上一个点和原点的坐标获得贝斯尔的控制点
    private float getBesPoint(float prex, float x) {
        return (prex + x) / 2;
    }

    private void touch_down(float downX, float downY) {
        float[] tempF = {downX, downY};
        Matrix tempMatrix = new Matrix();
        mMatrix.invert(tempMatrix);
        tempMatrix.mapPoints(tempF, new float[]{downX, downY});
        lineInfo = new LineInfo();
        lineInfo.setLineId(Build32Code.createGUID());
        if (mDrawState == DrawState.HANDWRITE || mDrawState == DrawState.PEN_YINGGUANG) {
            if (mDrawState == DrawState.HANDWRITE) {
                lineInfo.setColor(currntColor);
                lineInfo.setStrokeWidth(currentPaintSize);
                lineInfo.getCurrentPointLists().add(new PointInfo(null, transPointF.display2Logic(tempF[0], tempF[1]), 0));
//                paintInfo2Pc.sendDownInfo(lineInfo.getLineId(), Api.PresentControl.QuadraticBezier, currntColor, currentPaintSize,
//                        transPointF.display2RemoteX(tempF[0]), transPointF.display2RemoteY(tempF[test1]));
            } else if (mDrawState == DrawState.PEN_YINGGUANG) {
                int currentAlphaColor = ColorUtil.convertColorToString(currntColor, DrawPathCong.mYingGuangAlhpa);
                lineInfo.setColor(currentAlphaColor);
                lineInfo.setStrokeWidth(DrawPathCong.mYingGuangSize);
                lineInfo.getCurrentPointLists().add(new PointInfo(null, transPointF.display2Logic(tempF[0], tempF[1]), 0));
//                paintInfo2Pc.sendDownInfo(lineInfo.getLineId(), Api.PresentControl.PAINT_MARK_LINE, currentAlphaColor, DrawPathCong.mYingGuangAlhpa,
//                        transPointF.display2RemoteX(tempF[0]), transPointF.display2RemoteY(tempF[test1]));
            }
            mPaintLines.add(lineInfo);
            preX = tempF[0];
            preY = tempF[1];
        } else if (mDrawState == DrawState.ARROW || mDrawState == DrawState.ELLIPSE || mDrawState == DrawState.RECTANGLE) {
            lineInfo.setColor(currntColor);
            lineInfo.setStrokeWidth(currentPaintSize);
            lineInfo.getCurrentPointLists().add(new PointInfo(null, transPointF.display2Logic(tempF[0], tempF[1]), 0));
//            String drawType = Api.PresentControl.PAINT_ARROW_LINE;
            switch (mDrawState) {
                case ARROW:
                    lineInfo.setType(4);
//                    drawType = Api.PresentControl.PAINT_ARROW_LINE;
                    break;
                case ELLIPSE:
                    lineInfo.setType(3);
//                    drawType = Api.PresentControl.PAINT_ELLIPSE;
                    break;
                case RECTANGLE:
                    lineInfo.setType(2);
//                    drawType = Api.PresentControl.PAINT_RECTANGLE;
                    break;
            }
//            paintInfo2Pc.sendDownInfo(lineInfo.getLineId(), drawType, currntColor, currentPaintSize,
//                    transPointF.display2RemoteX(tempF[0]), transPointF.display2RemoteY(tempF[test1]));
            mPaintLines.add(lineInfo);
        }
        invalidate();
    }


    private void touch_move(float moveX, float moveY) {
        float dx = Math.abs(moveX - preX);
        float dy = Math.abs(moveY - preY);
        if (dx >= mTouchSlop || dy >= mTouchSlop) {   //对发送点的频率进行一定的限制
            float[] tempF = {moveX, moveY};
            Matrix tempMatrix = new Matrix();
            mMatrix.invert(tempMatrix);
            tempMatrix.mapPoints(tempF, new float[]{moveX, moveY});
            if (lineInfo == null) {
                Log.e("PaintImageView", "按压点还没有执行");
                return;
            }
            if (mDrawState == DrawState.HANDWRITE || mDrawState == DrawState.PEN_YINGGUANG) {
                PointInfo prePointInfo = lineInfo.getCurrentPointLists().get(lineInfo.getCurrentPointLists().size() - 1);
                float besX = getBesPoint(preX, tempF[0]);
                float besY = getBesPoint(preY, tempF[1]);
                lineInfo.getCurrentPointLists().add(new PointInfo(transPointF.display2Logic(preX, preY)
                        , transPointF.display2Logic(besX, besY), prePointInfo.getIndex() + 1));
                PointF lj_start = new PointF(transPointF.display2RemoteX(preX), transPointF.display2RemoteY(preY));
                PointF lj_ctrol = new PointF(transPointF.display2RemoteX(besX), transPointF.display2RemoteY(besY));
                paintInfo2Pc.sendMoveInfo(lineInfo.getLineId(), currentPoint,
                        lj_start.x, lj_start.y, lj_ctrol.x, lj_ctrol.y);
                invalidate();
                preX = tempF[0];
                preY = tempF[1];
            } else if (mDrawState == DrawState.RECTANGLE || mDrawState == DrawState.ARROW || mDrawState == DrawState.ELLIPSE) {
                if (lineInfo.getCurrentPointLists().size() > 1) {
//                    lineInfo.getCurrentPointLists().remove(lineInfo.getCurrentPointLists().size() - test1);
                    lineInfo.getCurrentPointLists().add(1, new PointInfo(null, transPointF.display2Logic(tempF[0], tempF[1]), 0));
                } else {
                    lineInfo.getCurrentPointLists().add(new PointInfo(null, transPointF.display2Logic(tempF[0], tempF[1]), 0));
                }
                paintInfo2Pc.setMoveShape(lineInfo.getLineId(), currentPoint, transPointF.display2RemoteX(tempF[0]), transPointF.display2RemoteY(tempF[1]));
                invalidate();
            }
            currentPoint++;
//            LogUtil.e("move",lineInfo.getLineId() +  "第" + currentPoint + "个点");
        }
    }

    private void touch_up() {
        currentPoint = 0;
        if (mCanvas == null) {
            Log.e("PaintImageView", "绘制bitmapCanvas == null");
            return;
        }
        drawCurrentPointPath(mCanvas);
        lineInfo = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (mDrawState == DrawState.NONE) {     //不处在放大状态,可以批注
            if (!isCanClick) {
                mScaleGestureDetector.onTouchEvent(event);
                if (!isScale) {
                    handleScaleMove(event);
                }
                return true;
            } else {
                return super.onTouchEvent(event);
            }
        } else if (mDrawState == DrawState.ERASER) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                String lineId = DrawUtil.getTouchLineId(x, y, transPointF, mPaintLines);
                if (TextUtils.isEmpty(lineId)) {
                    return true;
                }
                deleteId(lineId);
            }
            return true;
        } else {    //放大状态,不可批注
            getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isDraw = true;
                    touch_down(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isDraw = false;
                    touch_up();
                    break;
            }
            return true;
        }
    }

    private int lastPointerCount;
    private boolean isCanDrag;
    private float mLastX;
    private float mLastY;

    private void handleScaleMove(MotionEvent event) {
        float x = 0, y = 0;
        final int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;
        if (pointerCount != lastPointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        lastPointerCount = pointerCount;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                RectF rectF_down = getMatrixRectF();
                if (rectF_down.width() > getWidth() || rectF_down.height() > getHeight()) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                RectF rectF = getMatrixRectF();
                if (rectF.width() > getWidth() || rectF.height() > getHeight()) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                float dx = x - mLastX;
                float dy = y - mLastY;
                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }
                if (isCanDrag && getDrawable() != null) {
                    if (getMatrixRectF().left >= 0 && dx > 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    if (getMatrixRectF().right <= getWidth() && dx < 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    isCheckLeftAndRight = isCheckTopAndBottom = true;
                    if (rectF.width() < getWidth()) { // 如果宽度小于屏幕宽度，则禁止左右移动
                        dx = 0;
                        isCheckLeftAndRight = false;
                    }
                    if (rectF.height() < getHeight()) {// 如果高度小于雨屏幕高度，则禁止上下移动
                        dy = 0;
                        isCheckTopAndBottom = false;
                    }
                    float disX = dx;
                    float disY = dy;
                    if (dx > 0) {
                        if (dx > Math.abs(rectF.left) && isCheckLeftAndRight) {
                            dx = Math.abs(rectF.left);
                        }
                    } else {
                        if (dx < -(rectF.right - getWidth()) && isCheckLeftAndRight) {
                            dx = -(rectF.right - getWidth());
                        }
                    }
                    if (dy > 0) {
                        if (dy > Math.abs(rectF.top) - (mCentreTranY * mScale) && isCheckTopAndBottom) {
                            dy = Math.abs(rectF.top) - (mCentreTranY * mScale);
                        }
                    } else {
                        if (dy < -(rectF.bottom - getHeight()) && isCheckTopAndBottom) {
                            dy = -(rectF.bottom - getHeight());
                        }
                    }
                    if (pointerCount > 1) {
                        dx = 0;
                        dy = 0;
                        disX = 0;
                        disY = 0;
                    }
                    if (dx != 0 || dy != 0) {
                        mMatrix.postTranslate(dx, dy);
                        if (mBitmapWidth != 0 && mBitmapHeight != 0) {
                            paintInfo2Pc.sendTransLateInfo(disX / mBitmapWidth * 100, disY / mBitmapHeight * 100);
                        }
                        invalidate();
                    }
                }
                mLastX = x;
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                break;
        }
    }

    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
    }

    //根据id删除某条线
    private void deleteId(String lineId) {
        if (mPaintLines.size() > 0) {
            initDrawCanvas();
            Log.e("PaintImageView", "执行删除方法");
            for (int i = 0; i < mPaintLines.size(); i++) {
                if (TextUtils.equals(mPaintLines.get(i).getLineId(), lineId)) {
                    mPaintLines.remove(i);
                    paintInfo2Pc.sendUndoInfo(lineId);
                    break;
                }
            }
            drawAllPointPath(mCanvas);
            invalidate();
        } else {
            Toast.makeText(App.context, "没有信息要撤销哦", Toast.LENGTH_SHORT).show();
        }
    }

    //根据当前图片的Matrix获得图片的范围
    private RectF getMatrixRectF() {
        Matrix matrix = mMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    private boolean isCheckTopAndBottom = true;
    private boolean isCheckLeftAndRight = true;

    private boolean isScale = false;

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        float scale = getScale();
        float scaleFactor = scaleGestureDetector.getScaleFactor();
        mScale = getScale();
        if ((scale < DrawPathCong.MAX_SCALE && scaleFactor > 1.0f)
                || (scale > mStartScale && scaleFactor < 1.0f)) {
            if (scaleFactor * scale < mStartScale) {
                scaleFactor = mStartScale / scale;
            }
            if (scaleFactor * scale > DrawPathCong.MAX_SCALE) {
                scaleFactor = DrawPathCong.MAX_SCALE / scale;
            }
        }
        mMatrix.postScale(scaleFactor, scaleFactor,
                scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
        paintInfo2Pc.sendScaleInfo(mScale, transPointF.display2RemoteX(scaleGestureDetector.getFocusX()),
                transPointF.display2RemoteY(scaleGestureDetector.getFocusY()));
        invalidate();
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        isScale = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
        if (mScale < 1f || mScale > 5f) { //
            mMatrix.setScale(1f, 1f);
            mMatrix.postRotate(mCurrentRotate, getWidth() / 2, getHeight() / 2);
            paintInfo2Pc.clearMatrix();
            invalidate();
        }
        isCheckLeftAndRight = true;
        isScale = false;
    }

    /**
     * 通过矩阵映射图片上的(0,0)与(0,test1)点,
     * 获得图片的放大倍数
     */
    private float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(mMatrix, Matrix.MSCALE_X), 2) + (float) Math.pow(getValue(mMatrix, Matrix.MSKEW_Y), 2));
    }

    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    /**
     * 改变画笔信息,为简化逻辑,使用统一变量记录当前画笔颜色,粗细
     */
    private void changePaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(currntColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(currentPaintSize);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (drawable == null) {
            return;
        }
        mBitmapWidth = drawable.getIntrinsicWidth();
        mBitmapHeight = drawable.getIntrinsicHeight();
        mCentreTranX = (getWidth() - drawable.getIntrinsicWidth()) / 2;
        mCentreTranY = (getHeight() - drawable.getIntrinsicHeight()) / 2;
        transPointF = new TransPointF(mBitmapWidth, mBitmapHeight, mCentreTranX, mCentreTranY);
        if (mBitmapWidth > 0 || mBitmapHeight > 0) {
            initDrawCanvas();
            if (mCanvas != null) {
                drawAllPointPath(mCanvas);
            }
            if (isSendCordnate) {
                resetCordinate();
            }
            invalidate();
        }
    }

    //####################供外界使用的Api#################################
    //####################################################################
    public void setScale(float scale) {
        invalidate();
    }

    public void setCurrentColor(int color) {
        this.currntColor = color;
        changePaint();
    }

    /**
     * 清空
     */
    public void clear() { //调用初始化画布函数以清空画布
        initDrawCanvas();
        mPaintLines.clear();
        mRedoPaintLines.clear();
        paintInfo2Pc.sendMoveAllPath();
        invalidate();//刷新
    }

    /**
     * 撤销的核心思想就是将画布清空，
     * 将保存下来的Path路径最后一个移除掉，
     * 重新将路径画在画布上面。
     */
    public void undo() {
        if (mPaintLines.size() > 0) {
            initDrawCanvas();
            paintInfo2Pc.sendUndoInfo(mPaintLines.get(mPaintLines.size() - 1).getLineId());
            mRedoPaintLines.add(mPaintLines.get(mPaintLines.size() - 1));
            mPaintLines.remove(mPaintLines.size() - 1);
            drawAllPointPath(mCanvas);
            invalidate();
        } else {
            Toast.makeText(App.context, "没有信息要撤销了哦", Toast.LENGTH_SHORT).show();
        }
    }

    public void redo() {
        if (mRedoPaintLines.size() > 0) {
            initDrawCanvas();
            mPaintLines.add(mRedoPaintLines.get(mRedoPaintLines.size() - 1));
            paintInfo2Pc.sendRedoInfo(mRedoPaintLines.get(mRedoPaintLines.size() - 1).getLineId());
            mRedoPaintLines.remove(mRedoPaintLines.size() - 1);
            drawAllPointPath(mCanvas);
            invalidate();
        } else {
            Toast.makeText(App.context, "没有信息要恢复了哦", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置是否发送消息到pc客户端
     *
     * @param isSend true发送, FALSE,不发送
     */
    public void setIsSend2Client(boolean isSend) {
        paintInfo2Pc.setIsSend(isSend);
    }


    /**
     * 重置可绘制区域
     */
    public void resetCordinate() {
        paintInfo2Pc.resetCordinate(mBitmapWidth, mBitmapHeight);
    }

    private boolean isSendCordnate = false;

    public void setSendCordnate() {
        isSendCordnate = true;
        resetCordinate();
    }

    /**
     * 旋转90,会相叠加
     */
    public void rotate() {
        mCurrentRotate = (mCurrentRotate + 90);
        mMatrix.postRotate(90, getWidth() / 2, getHeight() / 2);
        invalidate();
        paintInfo2Pc.sendRotateInfo(90);
    }

    /**
     * 指定旋转的角度
     *
     * @param rotate
     */
    public void setRotate(int rotate) {
        mCurrentRotate = rotate;
        mMatrix.postRotate(0, getWidth() / 2, getHeight() / 2);
        mMatrix.postRotate(mCurrentRotate, getWidth() / 2, getHeight() / 2);
        invalidate();
        paintInfo2Pc.sendRotateInfo(mCurrentRotate);
    }

    /**
     * 获得当前图片的信息
     *
     * @return
     */
    public ArrayList<LineInfo> getDrawInfo() {
        return mPaintLines;
    }

    public void setDrawInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
        this.mPaintLines.clear();
        this.mPaintLines.addAll(pageInfo.getLineInfos());
        if (mCanvas != null) {
            drawAllPointPath(mCanvas);
        }
        invalidate();
    }

    /**
     * 清空变化信息,重置Matrix
     */
    public void reset() {
        mMatrix = new Matrix();
        invalidate();
    }

    public int getBitmapWidth() {
        return mBitmapWidth;
    }

    public int getBitmapHeight() {
        return mBitmapHeight;
    }

    public void setDrawRectangle() {
        mDrawState = DrawState.RECTANGLE;
    }

    //进入橡皮擦状态...
    public void setEraserState() {
        mDrawState = DrawState.ERASER;
    }

    //设置为普通笔绘画
    public void setDrawHandWrite() {
        mDrawState = DrawState.HANDWRITE;
    }

    public void setDrawArrow() {
        mDrawState = DrawState.ARROW;
    }

    public void setDrawEllipse() {
        mDrawState = DrawState.ELLIPSE;
    }

    public void setPenYingGuang() {
        mDrawState = DrawState.PEN_YINGGUANG;
    }

    /**
     * 设置当前状态可放大
     */
    public void setScaleState() {
        mDrawState = DrawState.NONE;
    }

    /**
     * 设置当前图片不可放大，可点击
     */
    public void setIsCanClick() {
        this.isCanClick = true;
    }


    /**
     * 设置当前画笔的粗细
     *
     * @param strokeWidth
     */
    public void setStrokeWidth(int strokeWidth) {
        this.currentPaintSize = strokeWidth;
        changePaint();
    }

}