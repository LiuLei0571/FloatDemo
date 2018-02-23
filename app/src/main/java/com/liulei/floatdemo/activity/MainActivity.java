package com.liulei.floatdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import com.liulei.floatdemo.FloatActionController;
import com.liulei.floatdemo.R;


/**
 * @author liulei
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {
    Button mBtnSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnSecond = findViewById(R.id.btn_second);
        mBtnSecond.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {

            case R.id.btn_second:
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unbindService(myServiceConnection);
        FloatActionController.getInstance().stopMonkServer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FloatActionController.getInstance().stopMonkServer(this);

    }
}
