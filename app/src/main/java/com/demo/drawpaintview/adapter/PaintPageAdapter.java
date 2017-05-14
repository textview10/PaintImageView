package com.demo.drawpaintview.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.demo.drawpaintview.R;
import com.demo.drawpaintview.paint.PaintImageView;
import com.demo.drawpaintview.paint.bean.PageInfo;
import com.demo.drawpaintview.ui.activity.MainActivity;

import java.util.ArrayList;

/**
 * Created by：xu.wang on 2017/5/14 11:17
 */

public class PaintPageAdapter extends PagerAdapter {
    private ArrayList<PageInfo> mLists;
    private MainActivity mActivity;
    private int[] res = {
            R.drawable.test, R.drawable.test1, R.drawable.test2,R.drawable.hoi4
    };

    public PaintPageAdapter(Context context, ArrayList<PageInfo> lists) {
        mActivity = (MainActivity) context;
        this.mLists = lists;
    }

    @Override
    public int getCount() {
        return mLists == null ? 0 : mLists.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public PaintImageView instantiateItem(ViewGroup container, final int position) {
        final PageInfo pageInfo = mLists.get(position);
        final PaintImageView drawPaintView = new PaintImageView(container.getContext());
        container.addView(drawPaintView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        drawPaintView.setTag(position);
        if (mActivity.isFirst) {
            mActivity.mCurrentImageView = drawPaintView;
        }
        Glide.with(container.getContext()).load(res[position]).error(R.drawable.test).into(drawPaintView);
        //设置历史批注信息
        if (pageInfo.getLineInfos().size() > 0) {
            drawPaintView.setDrawInfo(mLists.get(position));
        }
        return drawPaintView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((PaintImageView) object);
    }
}
