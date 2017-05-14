package com.demo.drawpaintview;

import android.app.Application;
import android.content.Context;

/**
 * Created by：xu.wang on 2017/5/14 10:49
 */

public class App extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
