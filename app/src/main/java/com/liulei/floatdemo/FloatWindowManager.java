package com.liulei.floatdemo;

import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;

import com.liulei.floatdemo.view.FloatLayout;

import java.util.List;

/**
 * 用途：.
 *
 * @author ：Created by liulei.
 * @date 2018/2/7
 */


public class FloatWindowManager {
    private static FloatLayout mFloatLayout;
    private static WindowManager mWindowManager;
    private static WindowManager.LayoutParams wmParams;
    private static boolean mHasShown;

    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右下角位置。
     */
    public static void createFloatWindow(final Context context) {
        wmParams = new WindowManager.LayoutParams();
        WindowManager windowManager = getWindowManager(context);
        mFloatLayout = new FloatLayout(context);
        mFloatLayout.setmListener(new FloatViewListener() {
            @Override
            public void reBack(Object object) {
                FloatActionController.getInstance().stopMonkServer(context);
                String packageName = getTopAppPackageName(context.getApplicationContext());
                doStartApplicationWithPackageName(context, "com.getui.headad.headad");
            }

            @Override
            public void closeFloat(Object object) {
                FloatActionController.getInstance().stopMonkServer(context);
            }
        });
        if (Build.VERSION.SDK_INT >= 26 && MyApplication.context.getApplicationInfo().targetSdkVersion > 22) {
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

        } else {
            PackageManager pm = context.getPackageManager();
            boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", context.getPackageName()));
            if (permission || "Xiaomi".equals(Build.MANUFACTURER) || "vivo".equals(Build.MANUFACTURER)) {
                wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            } else {
                wmParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            }

        }
        wmParams.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.gravity = Gravity.CENTER | Gravity.START;
        DisplayMetrics dm = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(dm);

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mFloatLayout.setParams(wmParams);
        mHasShown = true;
        try {
            windowManager.addView(mFloatLayout, wmParams);
        } catch (Exception e) {

        }
    }

    /**
     * 返回当前已创建的WindowManager。
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    public static void removeFloatWindowManager() {
        //移除悬浮窗口
        boolean isAttach = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isAttach = mFloatLayout.isAttachedToWindow();
        }
        if (mHasShown && isAttach && mWindowManager != null) {
            mWindowManager.removeView(mFloatLayout);
        }
    }

    public static void hide() {
        if (mHasShown) {
            mWindowManager.removeViewImmediate(mFloatLayout);
        }
        mHasShown = false;
    }

    public static void show() {
        if (!mHasShown && mFloatLayout != null) {
            mWindowManager.addView(mFloatLayout, wmParams);
        }
        mHasShown = true;
    }

    public static String getTopAppPackageName(Context context) {
        String packageName = "";
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                List<ActivityManager.RunningTaskInfo> rti = activityManager.getRunningTasks(1);
                packageName = rti.get(0).topActivity.getPackageName();
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
                List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
                if (processes.size() == 0) {
                    return packageName;
                }
                for (ActivityManager.RunningAppProcessInfo process : processes) {
                    if (process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return process.processName;
                    }
                }
            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                final long end = System.currentTimeMillis();
                final UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                if (null == usageStatsManager) {
                    return packageName;
                }
                final UsageEvents events = usageStatsManager.queryEvents((end - 60 * 1000), end);
                if (null == events) {
                    return packageName;
                }
                UsageEvents.Event usageEvent = new UsageEvents.Event();
                UsageEvents.Event lastMoveToFGEvent = null;
                while (events.hasNextEvent()) {
                    events.getNextEvent(usageEvent);
                    if (usageEvent.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                        lastMoveToFGEvent = usageEvent;
                    }
                }
                if (lastMoveToFGEvent != null) {
                    packageName = lastMoveToFGEvent.getPackageName();
                }
            }
        } catch (Exception ignored) {
        }
        return packageName;
    }

    private static void doStartApplicationWithPackageName(Context mContext, String packagename) {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = mContext.getPackageManager().getPackageInfo(packagename, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = mContext.getPackageManager()
                .queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);
            intent.setComponent(cn);
            mContext.startActivity(intent);
        }
    }

    private boolean hasExternalStoragePermission(Context context, String permissionName) {
        int perm = context.checkCallingOrSelfPermission(permissionName);
        return perm == PackageManager.PERMISSION_GRANTED;
    }
}
