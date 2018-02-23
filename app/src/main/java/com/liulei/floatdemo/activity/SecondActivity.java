package com.liulei.floatdemo.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.liulei.floatdemo.FloatActionController;
import com.liulei.floatdemo.MyApplication;
import com.liulei.floatdemo.R;
import com.liulei.floatdemo.util.MIDevierUtil;
import com.liulei.floatdemo.util.MeiZuDevierUtil;

/**
 * 用途：.
 *
 * @author ：Created by liulei.
 * @date 2018/2/8
 */


public class SecondActivity extends Activity {
    Button mBtnTestJump;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        mBtnTestJump = findViewById(R.id.btn_test_jump);
        PackageManager pm = getPackageManager();
        final boolean permission = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.SYSTEM_ALERT_WINDOW", getPackageName()));
        mBtnTestJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (("Xiaomi".equals(Build.MANUFACTURER) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)) {
                    requestMIPermission();
                } else if ("Meizu".equals(Build.MANUFACTURER) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && !permission) {
                    requestMeizuPermission();

                } else if ("vivo".equals(Build.MANUFACTURER) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    switchActivity();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && MyApplication.context.getApplicationInfo().targetSdkVersion > 22) {
                        if (isOverDraw()) {
                            switchActivity();
                        }
                    } else {
                        switchActivity();
                    }
                }
            }
        });
    }


    public void handleDeepLink(Context context, String pkgName, String deepLink) {
        Uri uri = Uri.parse(deepLink);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PackageManager packageManager = this.getPackageManager();
        intent = packageManager.getLaunchIntentForPackage(pkgName);
        if (!TextUtils.isEmpty(pkgName)) {
            intent.setPackage(pkgName);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FloatActionController.getInstance().stopMonkServer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatActionController.getInstance().stopMonkServer(this);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isOverDraw() {
        if (!Settings.canDrawOverlays(SecondActivity.this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 10);
            return false;
        }
        return true;
    }

    /**
     * 请求用户给予悬浮窗的权限
     */
    public void requestMIPermission() {
        if (MIDevierUtil.isFloatWindowOpAllowed(this)) {//已经开启
            switchActivity();
        } else {
            MIDevierUtil.openSetting(this);
        }
    }

    /**
     * 请求用户给予悬浮窗的权限
     */
    public void requestMeizuPermission() {
        if (MeiZuDevierUtil.checkFloatWindowPermission(this)) {//已经开启
            switchActivity();
        } else {
            MeiZuDevierUtil.applyPermission(this);
        }
    }

    private void switchActivity() {
        handleDeepLink(SecondActivity.this, "com.liulei.deeplink", "http//www.baidu.com");
        FloatActionController.getInstance().startMonkServer(this);
    }

}
