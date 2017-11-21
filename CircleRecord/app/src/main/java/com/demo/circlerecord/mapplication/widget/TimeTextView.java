package com.demo.circlerecord.mapplication.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.TextView;

import com.demo.circlerecord.mapplication.R;

/**
 * Created by yz_wuhen on 2017/11/21.
 */

public class TimeTextView extends TextView {

    private int sTime;
    private TimeTextView timeTextView;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            sTime++;
            timeTextView.setText(String.valueOf(sTime)+"s: 60s");
            handler.sendEmptyMessageDelayed(1,1000);
        }
    };
    public TimeTextView(Context context) {
        super(context);
        initView();
    }

    public TimeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TimeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    //初始化，即一开始显示的点击说话
    private void initView() {
        timeTextView= this;
        timeTextView.setText(getContext().getResources().getString(R.string.click_say));
    }

    public void downTime(){
        handler.sendEmptyMessage(1);
    }
    //停止计时后，记得把handler remove掉
    public void stopTime(){
        sTime=0;
        handler.removeCallbacksAndMessages(null);
    }

    //取消之后恢复文字说明
    public void cancleTime(){
        timeTextView.setText(getContext().getResources().getString(R.string.click_say));
    }
    public int getsTime(){
        return sTime;
    }
}
