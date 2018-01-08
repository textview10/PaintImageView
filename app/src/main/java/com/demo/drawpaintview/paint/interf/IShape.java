package com.demo.drawpaintview.paint.interf;

/**
 * Created by xu.wang
 * Date on  2017/11/7 10:59:48.
 *
 * @Desc 所有线条的基础类
 */

public interface IShape {
    //0,贝塞尔曲线, 1,荧光笔,2, 矩形 , 3 椭圆, 4,箭头 5,田字格,6米字格,7,四线格8,直线
    int SHAPE_BEZIER = 0;    //贝塞尔曲线
    int SHAPE_PEN_YINGGUANG = 1;    //荧光笔
    int SHAPE_RECTANGLE = 2;    //矩形
    int SHAPE_ELLIPSE = 3;  //椭圆
    int SHAPE_ARROW = 4;    //箭头
    int SHAPE_TIZIGE = 5;   //田字格
    int SHAPE_MIZIGE = 6;   //米字格
    int SHAPE_SIXIANGE = 7; //四线格
    int SHAPE_POLYLINE = 8; //直线

}
