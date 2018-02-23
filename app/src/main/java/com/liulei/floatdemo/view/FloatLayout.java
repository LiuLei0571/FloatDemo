package com.liulei.floatdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.liulei.floatdemo.FloatViewListener;
import com.liulei.floatdemo.R;


/**
 * @author liulei
 */
public class FloatLayout extends FrameLayout {
    private final WindowManager mWindowManager;
    private boolean isclick;
    private WindowManager.LayoutParams mWmParams;
    private Context mContext;
    private TextView mTvClose;
    private TextView mTvReturn;
    private FloatViewListener mListener;

    public void setmListener(FloatViewListener mListener) {
        this.mListener = mListener;
    }

    public FloatLayout(Context context) {
        this(context, null);
        mContext = context;
    }

    public FloatLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        View mView = LayoutInflater.from(context).inflate(R.layout.float_littlemonk_layout, this);
        //浮动窗口按钮
        mTvClose = mView.findViewById(R.id.tv_close);
        mTvReturn = mView.findViewById(R.id.tv_return);
        mTvClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.closeFloat(null);
            }
        });
        mTvReturn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.reBack(null);
            }
        });

    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mWmParams = params;
    }
}
