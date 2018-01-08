package com.demo.drawpaintview.ui.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.demo.drawpaintview.R;
import com.demo.drawpaintview.adapter.PaintPageAdapter;
import com.demo.drawpaintview.paint.PaintImageView;
import com.demo.drawpaintview.paint.bean.PageInfo;
import com.demo.drawpaintview.paint.bean.shape.LineInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xu.wang
 * Date on 2017/test2/18 20:02
 * modify on 2017/5/14
 */
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.btn_main_scale)
    AppCompatButton btn_scale;
    @BindView(R.id.btn_main_rectangle)
    AppCompatButton btn_rectangle;
    @BindView(R.id.btn_main_undo)
    AppCompatButton btn_undo;
    @BindView(R.id.btn_main_redo)
    AppCompatButton btn_redo;
    @BindView(R.id.btn_main_clear)
    AppCompatButton btn_clear;
    @BindView(R.id.btn_main_arrow)
    AppCompatButton btn_arrow;
    @BindView(R.id.btn_main_ellipse)
    AppCompatButton btn_ellipse;

    public int mCurrentPosition = 0;
    public PaintImageView mCurrentImageView;   //当前的paintImageView;
    public boolean isFirst = true;

    private enum DRAW_STATE {
        NONE, ERASER, DRAW_BEZIER, DRAW_RECTANGLE
    }

    private DRAW_STATE mDrawState = DRAW_STATE.NONE;


    private ArrayList<PageInfo> lists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initialData();
        initialListener();
    }

    private void initialData() {
        lists.clear();
        for (int i = 0; i < 4; i++) {
            lists.add(new PageInfo(i, new ArrayList<LineInfo>(), null));
        }
        PaintPageAdapter mAdapter = new PaintPageAdapter(this, lists);
        mViewPager.setAdapter(mAdapter);
    }

    private void initialListener() {
        mViewPager.setOnPageChangeListener(new MyPageListener());
    }

    private void getCurrentPage() {
        mCurrentImageView = (PaintImageView) mViewPager.findViewWithTag(mCurrentPosition);
    }

    @OnClick({R.id.btn_main_scale, R.id.btn_main_undo, R.id.btn_main_redo, R.id.btn_main_arrow,
            R.id.btn_main_rectangle, R.id.btn_main_clear, R.id.btn_main_line,R.id.btn_main_ellipse})
    void OnClick(View view) {
        getCurrentPage();
        if (mCurrentImageView == null) {
            Log.e("MainActivity", "没有获取到当前对象");
            return;
        }
        switch (view.getId()) {
            case R.id.btn_main_scale:
                if (mDrawState == DRAW_STATE.NONE) {

                } else {
                    mDrawState = DRAW_STATE.NONE;
                    mCurrentImageView.setScaleState();
                }
                break;
            case R.id.btn_main_undo:
                mCurrentImageView.undo();
                break;
            case R.id.btn_main_redo:
                mCurrentImageView.redo();
                break;
            case R.id.btn_main_arrow:
                mCurrentImageView.setDrawArrow();
                break;
            case R.id.btn_main_rectangle:
                if (mDrawState == DRAW_STATE.DRAW_RECTANGLE) {

                } else {
                    mDrawState = DRAW_STATE.DRAW_RECTANGLE;
                    mCurrentImageView.setDrawRectangle();
                }
                break;
            case R.id.btn_main_clear:
                mCurrentImageView.clear();
                break;
            case R.id.btn_main_line:
                if (mDrawState == DRAW_STATE.DRAW_BEZIER) {

                } else {
                    mDrawState = DRAW_STATE.DRAW_BEZIER;
                    mCurrentImageView.setDrawHandWrite();
                }
                break;
            case R.id.btn_main_ellipse:
                mCurrentImageView.setDrawEllipse();
                break;
        }
    }

    class MyPageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            restorePageInfo();
            mCurrentPosition = position;
            getCurrentPage();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    private void restorePageInfo() {
        getCurrentPage();
        if (mCurrentImageView != null && lists.get(mCurrentPosition) != null) {
            mCurrentImageView.reset();
            lists.get(mCurrentPosition).getLineInfos().clear();
            lists.get(mCurrentPosition).getLineInfos().addAll(mCurrentImageView.getDrawInfo());
        } else {
            Log.e("MainActivity", "PaintImageView为空");
        }
    }
}
