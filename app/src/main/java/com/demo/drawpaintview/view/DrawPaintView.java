package com.demo.drawpaintview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;

import com.demo.drawpaintview.util.BitmapUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import cn.forward.androids.utils.ImageUtils;

/**
 * Created by xu.wang
 * Date on 2017/1/19 14:06
 */
public class DrawPaintView extends View implements ScaleGestureDetector.OnScaleGestureListener {
    private String TAG = getClass().getSimpleName().toString();
    private Context mContext;
    private Bitmap mBitmap;
    private Bitmap mViewBitmap;
    private Canvas mCanvas;         //单缓存缓存历史线条
    private Bitmap mCanvasBitmap; //用来绘制线条的bitmap
    private Paint mPaint;
    private ArrayList<LineInfo> mPaintLines;
    private int currntColor; //记录画笔颜色
    private int currentPaintSize; //记录画笔粗细
    private ScaleGestureDetector mScaleGestureDetector = null;  //缩放检查手势的类
    private GestureDetector mGestureListener = null;
    private int currentPoint = 0;   //当前是本次绘制的第几个点
    private float mScale; // 缩放倍数, mScale
    private float mBitmapScale; //图片自适应控件造成的缩放倍数
    private Matrix mMatrix = new Matrix();     //
    private float[] mMatrixValues = new float[9];
    private float preX, preY = 0;   //上一个点的位置
    private int mBitmapWidth, mBitmapHeight;
    private float mCentreTranX, mCentreTranY;// 图片初始化居中时的偏移
    private PaintInfo2Pc paintInfo2Pc;      //向pc端发送消息的对象
    private boolean isScaleState = false;    //是否放大
    private int mTouchSlop;             //最小滑动距离
    private File mFile;
    private int mCurrentRotate = 0;
    private DrawState mDrawState = DrawState.HANDWRITE; //当前绘制状态

    public enum DrawState {
        //手写,直线 ,带箭头的直线,正方形,圆形
        HANDWRITE, Line, ARROW_LINE, RECTANGLES, OVAL
    }

    public DrawPaintView(Context context) {
        this(context, null);
    }

    public DrawPaintView(Context context, AttributeSet attrs) {
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
        Collections.synchronizedList(mPaintLines);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        initPaint();
        mScaleGestureDetector = new ScaleGestureDetector(mContext, this);
        mGestureListener = new GestureDetector(mContext, new DrawPathGestureListener());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setBg();
    }

    //调整图片的宽高,设置给控件...
    private void setBg() {
        if (mFile != null){
            mBitmap = ImageUtils.createBitmapFromPath(mFile.getPath(), mContext);
        }
        if (mBitmap == null){
            Log.e(TAG,"没有设置图片对象");
            return;
        }
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float nw = w * 1f / getWidth();
        float nh = h * 1f / getHeight();
        if (nw > nh) {
            mBitmapScale = 1 / nw;
            mBitmapWidth = getWidth();
            mBitmapHeight = (int) (h * mBitmapScale);
        } else {
            mBitmapScale = 1 / nh;
            mBitmapWidth = (int) (w * mBitmapScale);
            mBitmapHeight = getHeight();
        }
        // 使图片居中
        mCentreTranX = (getWidth() - mBitmapWidth) / 2f;
        mCentreTranY = (getHeight() - mBitmapHeight) / 2f;
        mViewBitmap = BitmapUtil.scaleBitmap(mBitmap, mBitmapWidth, mBitmapHeight);
        mBitmap.recycle();
        initDrawCanvas();
        invalidate();
    }

    //初始化画板,为简化逻辑,使用统一变量记录当前画笔颜色,粗细
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(currntColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(currentPaintSize);
    }

    //初始化画布
    private void initDrawCanvas() {
        mCanvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mCanvasBitmap);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mViewBitmap == null || mViewBitmap.isRecycled()) {
            Log.e(TAG,"mViewBitmap被回收");
            return;
        }
        if (mMatrix != null) {
            canvas.setMatrix(mMatrix);
        }
        canvas.drawBitmap(mViewBitmap, mCentreTranX, mCentreTranY, mPaint);
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mPaint);

        if (mDrawState == DrawState.HANDWRITE) {
            drawCurrentPointPath(canvas);
        } else {
            drawPicture(canvas);
        }
    }

    private void drawPicture(Canvas canvas) {
        switch (mDrawState) {
            case Line:

                break;
            case ARROW_LINE:
                break;
            case RECTANGLES:
                break;
            case OVAL:
                break;
        }
    }

    private void drawCurrentPointPath(Canvas canvas) {
        if (mPaintLines.size() < 1) {
            return;
        }
        LineInfo lineInfo = mPaintLines.get(mPaintLines.size() - 1);
        ArrayList<PointInfo> pointInfos = lineInfo.getCurrentPointLists();
        Path path = new Path();
        for (int i = 0; i < pointInfos.size(); i++) {
            PointF pointF = pointInfos.get(i).getPointF();
            if (i == 0) {
                path.moveTo(pointF.x, pointF.y);
            } else {
                PointF prePointF = pointInfos.get(i).getPrePointF();
//                    path.lineTo(pointF.x, pointF.y);  //绘制直线
                float besX = getBesPoint(prePointF.x, pointF.x);
                float besY = getBesPoint(prePointF.y, pointF.y);
                path.quadTo(prePointF.x, prePointF.y, besX, besY);     //绘制贝斯尔曲线

            }

            canvas.drawPath(path, lineInfo.getPaint());
        }
    }

    //向画板上画全部记录的全部点的轨迹,绘制过程比较慢...
    private void drawAllPointPath(Canvas cavas) {
        Path path = new Path();
        for (int i = 0; i < mPaintLines.size(); i++) {
            LineInfo lineInfo = mPaintLines.get(i);
            ArrayList<PointInfo> pointInfos = lineInfo.getCurrentPointLists();
            for (int j = 0; j < pointInfos.size(); j++) {
                PointF pointF = pointInfos.get(j).getPointF();
                if (j == 0) {
                    path.moveTo(pointF.x, pointF.y);
                } else {
                    PointF prePointF = pointInfos.get(j).getPrePointF();
//                    path.lineTo(pointF.x, pointF.y);  //绘制直线
                    float besX = getBesPoint(prePointF.x, pointF.x);
                    float besY = getBesPoint(prePointF.y, pointF.y);
                    path.quadTo(prePointF.x, prePointF.y, besX, besY);     //绘制贝斯尔曲线
                }
                cavas.drawPath(path, lineInfo.getPaint());
            }
        }
    }

    // 根据上一个点和原点的坐标获得贝斯尔的控制点
    private float getBesPoint(float prex, float x) {
        return (prex + x) / 2;
    }

    private void touch_down(float downX, float downY) {
        LineInfo lineInfo = new LineInfo();
        lineInfo.setLineId("1111");
        lineInfo.getPaint().setColor(currntColor);
        lineInfo.getPaint().setStrokeWidth(currentPaintSize);
        lineInfo.getPaint().setStyle(Paint.Style.STROKE);
        float[] tempF = {downX, downY};
        Matrix tempMatrix = new Matrix();
        mMatrix.invert(tempMatrix);
        tempMatrix.mapPoints(tempF, new float[]{downX, downY});
        lineInfo.getCurrentPointLists().add(new PointInfo(null, new PointF(tempF[0], tempF[1]), 0));
        paintInfo2Pc.sendDownInfo(lineInfo.getLineId(), currntColor, currentPaintSize,
                toX4Pc(tempF[0]), toY4Pc(tempF[1]));
        mPaintLines.add(lineInfo);
        preX = downX;
        preY = downY;
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
            LineInfo lineInfo = mPaintLines.get(mPaintLines.size() - 1);
            PointInfo prePointInfo = lineInfo.getCurrentPointLists().get(lineInfo.getCurrentPointLists().size() - 1);
            float besX = getBesPoint(prePointInfo.getPointF().x, toX(tempF[0]));
            float besY = getBesPoint(prePointInfo.getPointF().y, toY(tempF[1]));
            lineInfo.getCurrentPointLists().add(new PointInfo(prePointInfo.getPointF()
                    , new PointF(toX(tempF[0]), toY(tempF[1])), prePointInfo.getIndex() + 1));
            paintInfo2Pc.sendMoveInfo(mPaintLines.get(mPaintLines.size() - 1).getLineId(), currentPoint, toX4Pc(prePointInfo.getPointF().x),
                    toY4Pc(prePointInfo.getPointF().y), toX4Pc(besX), toY4Pc(besY));
            preX = moveX;
            preY = moveY;
        }
        invalidate();
        currentPoint++;
    }

    private void touch_up() {
        currentPoint = 0;
        drawCurrentPointPath(mCanvas);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if (!isScaleState) {     //不处在放大状态,可以批注
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_down(x, y);
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    touch_up();
                    break;
            }
        } else {    //放大状态,不可批注
            if (event.getPointerCount() > 1) {  //放大状态则处理放大手势
                mScaleGestureDetector.onTouchEvent(event);
            } else {
                if (getScale() > 1 && !isScale) {   //放大后处理拖动手势
                    mGestureListener.onTouchEvent(event);
                }
            }
        }
        return true;
    }

    private boolean isScale = false;

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        float scaleFactor = scaleGestureDetector.getScaleFactor();
        mScale = getScale();
        mMatrix.postScale(scaleFactor, scaleFactor,
                scaleGestureDetector.getFocusX(), scaleGestureDetector.getFocusY());
        paintInfo2Pc.sendScaleInfo(mScale, scaleGestureDetector.getFocusX(),
                scaleGestureDetector.getFocusY());
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
        if (mScale < 1f || mScale > 2.5f) {
            mMatrix.setScale(1f, 1f);
            mMatrix.postRotate(mCurrentRotate, getWidth() / 2, getHeight() / 2);
            paintInfo2Pc.sendScaleInfo(1f, 0f, 0f);
            invalidate();
        }
        isScale = false;
    }

    /**
     * 通过矩阵映射图片上的(0,0)与(0,1)点,
     * 获得图片的放大倍数
     */
    private float getScale() {
        return (float) Math.sqrt((float) Math.pow(getValue(mMatrix, Matrix.MSCALE_X), 2) + (float) Math.pow(getValue(mMatrix, Matrix.MSKEW_Y), 2));
    }

    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    class DrawPathGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) < 300 && Math.abs(distanceY) < 300) {
                mMatrix.postTranslate((0 - distanceX), (0 - distanceY));
                paintInfo2Pc.sendTransLateInfo((0 - distanceX), (0 - distanceY));
                invalidate();
            }
            return false;
        }
    }

    //改变画笔信息,为简化逻辑,使用统一变量记录当前画笔颜色,粗细
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

    private float toX(float touchX) {
        if (touchX < mCentreTranX) {
            touchX = mCentreTranX;
        }
        if (touchX > mCentreTranX + mBitmapWidth) {
            touchX = mCentreTranX + mBitmapWidth;
        }
        return touchX;
    }

    private float toY(float touchY) {
        if (touchY < mCentreTranY) {
            touchY = mCentreTranY;
        }
        if (touchY > mCentreTranY + mBitmapHeight) {
            touchY = mCentreTranY + mBitmapHeight;
        }
        return touchY;
    }

    //转化为pc端的坐标
    private float toX4Pc(float x) {
        return (x - mCentreTranX);
    }

    private float toY4Pc(float y) {
        return (y - mCentreTranY);
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
    public void clear() {
        //调用初始化画布函数以清空画布
        initDrawCanvas();
        mPaintLines.clear();
        paintInfo2Pc.sendMoveAllPath();
        invalidate();//刷新
    }

    /**
     * 撤销上一步
     */
    public void undo() {
        initDrawCanvas();
        if (mPaintLines.size() > 0) {
            paintInfo2Pc.sendUndoInfo(mPaintLines.get(mPaintLines.size() - 1).getLineId());
            mPaintLines.remove(mPaintLines.size() - 1);
            drawAllPointPath(mCanvas);
        } else {
            Toast.makeText(mContext,"没有信息要撤销了哦",Toast.LENGTH_SHORT).show();
        }
        invalidate();
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
     * 设置显示图片资源
     *
     * @param file
     */
    public void setImagePath(File file) {
        this.mFile = file;
    }

    public void setImageBitmap(Bitmap bitmap){
        this.mBitmap = bitmap;
    }

    /**
     * 发送消息让pc端显示某张图片
     */
    public void send2ShowPicture(String name) {
        paintInfo2Pc.send2ShowPicture(name);
    }

    /**
     * 是否处于可放大的状态
     * false不可放大,处于批注状态,true可放大,不可批注.
     */
    public void setIsScale(boolean isScaleState) {
        this.isScaleState = isScaleState;
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

    public void setDrawInfo(ArrayList<LineInfo> lineInfos) {
        this.mPaintLines = lineInfos;
        initDrawCanvas();
        drawAllPointPath(mCanvas);
        invalidate();
    }

}
