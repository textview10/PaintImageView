package com.demo.drawpaintview.paint.interf;

/**
 * Created by xu.wang
 * Date on 2017/9/7 11:07
 */

public interface OnRefreshListner {
    void refresh(boolean isCreate);     //是否需要销毁当前缓存区Bitmap重新绘制
}
