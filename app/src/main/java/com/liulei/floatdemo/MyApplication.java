package com.liulei.floatdemo;

import android.app.Application;
import android.content.Context;

/**
 * 用途：.
 *
 * @author ：Created by liulei.
 * @date 2018/2/8
 */


public class MyApplication extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
