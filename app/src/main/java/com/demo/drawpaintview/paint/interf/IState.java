package com.demo.drawpaintview.paint.interf;

/**
 * Created by xu.wang
 * Date on 2017/7/17 15:43
 * 画板绘制类似
 */

public interface IState {
    void setLineEraserState();      //橡皮,擦除某一笔

    void setEraserRect();           //擦除一个区域

    void setDrawHandWrite();        //绘制贝塞尔

    void setDrawPolyLine();         //绘制折线

    void setDrawRectangle();        //绘制正方形

    void setDrawArrow();            //绘制箭头

    void setDrawEllipse();          //绘制椭圆

    void setPenYingGuang();         //绘制荧光笔

    void setTianZiGe();             //绘制田字格

    void setMiZiGe();               //绘制米字格

    void setSiXianGe();             //绘制四线格

    void setScaleState();           //如果是PaintBlackBoardView,则退出绘制状态.PaintImageView,则退出绘制状态,进入放大状态

    void setBreakResult();          //打断模式...
}
