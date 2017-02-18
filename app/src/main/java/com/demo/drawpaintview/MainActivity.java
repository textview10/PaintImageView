package com.demo.drawpaintview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;

import com.demo.drawpaintview.view.DrawPaintView;
import com.demo.drawpaintview.view.LineInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by xu.wang
 * Date on 2017/2/18 20:02
 */
public class MainActivity extends AppCompatActivity {
    @BindView(R.id.dpv_main_test)
    DrawPaintView mDrawPaintView;
    @BindView(R.id.btn_main_scale)
    AppCompatButton btn_scale;
    @BindView(R.id.btn_main_save)
    AppCompatButton btn_save;

    private ArrayList<LineInfo> lists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initialView();
        initialListener();
    }

    private void initialView() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
        mDrawPaintView.setImageBitmap(bitmap);
    }

    private void initialListener() {
        btn_scale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.equals(btn_scale.getText().toString(), "放大")) {
                    btn_scale.setText("绘制");
                    mDrawPaintView.setIsScale(false);
                } else {
                    btn_scale.setText("放大");
                    mDrawPaintView.setIsScale(true);
                }
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.equals(btn_save.getText().toString(), "保存")) {
                    btn_save.setText("恢复");
                    lists.clear();
                    lists.addAll(mDrawPaintView.getDrawInfo());
                    mDrawPaintView.clear();
                } else {
                    btn_scale.setText("保存");
                    mDrawPaintView.setDrawInfo(lists);
                }
            }
        });
    }

    //向右旋转90度
    @OnClick(R.id.btn_main_rotate)
    void rotate() {
        mDrawPaintView.rotate();
    }

    //撤销
    @OnClick(R.id.btn_main_undo)
    void undo() {
        mDrawPaintView.undo();
    }

    //清空
    @OnClick(R.id.btn_main_clear)
    void clear() {
        mDrawPaintView.clear();
    }
}
