package com.demo.drawpaintview.paint.interf;

/**
 * Created by xu.wang
 * Date on  2017/11/28 11:52:55.
 *
 * @Desc
 */

public interface OnUndoRedoChangeListener {
    void OnChange(boolean canUndo, boolean canRedo);   //true,接下来还可以撤销,false接下来不可以撤销了

}
