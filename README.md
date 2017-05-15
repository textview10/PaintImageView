
Android 可以在ViewPager中使用的可以画线,放大,保存,点击删除线条的自定义图片显示控件

详情请查看

http://blog.csdn.net/baidu_33546245/article/details/71963834

# DrawPaintView 调用api

setScale(float scale)	//设置当前图片在自适应控件后的放大倍数,尚不能使用

setCurrentColor(int color)  //设置当前画笔颜色,下一笔生效

clear()			//清空画布

undo()			//撤销上一笔

setImagePath(File file);		//给自定义控件设置要显示的图片

setIsScale(boolean isScaleState)	//设置当前是放大状态还是批注状态

rotate()				//在当前状态的基础上向右旋转90度,控件会记住旋转状态并叠加(第二次点击就会在旋转90的基础上再旋转90)

setRotate(int rotate)			//设置旋转到某个角度,暂时会清空之前的放大状态,待优化.

setDrawInfo(ArrayList<LineInfo> lineInfos)	//将之前的图片绘制信息重新设置给图片..

public ArrayList<LineInfo> getDrawInfo()       //获得当前图片绘制的详情,可用于将批注的线条信息暂时保存在内存中.
