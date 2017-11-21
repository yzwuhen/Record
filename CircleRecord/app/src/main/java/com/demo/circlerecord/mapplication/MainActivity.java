package com.demo.circlerecord.mapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.demo.circlerecord.mapplication.utils.PopWindowUtils;
import com.demo.circlerecord.mapplication.widget.SocketRecordVoiceView;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{
    PopWindowUtils popWindowUtils;
    private SocketRecordVoiceView socketRecordVoiceView;
    private TextView mTvText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvText = (TextView) findViewById(R.id.tv_text);
        mTvText.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (popWindowUtils==null){
            popWindowUtils = PopWindowUtils.getInstance();
            popWindowUtils.general(this, new PopWindowUtils.onViewListener() {
                @Override
                public void getView(PopupWindow pop, View view, View parent) {
                    socketRecordVoiceView = (SocketRecordVoiceView) view.findViewById(R.id.cus_record_voice);
                    socketRecordVoiceView.setSocketTouchCall(new SocketRecordVoiceView.SocketVoiceRecorderCallback() {
                        @Override
                        public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                            //   android.util.Log.v("Sssss",voiceTimeLength+"ssssssssssssssssss視頻路徑"+voiceFilePath);
                        }
                    });
                    socketRecordVoiceView.setBackOnClick(new SocketRecordVoiceView.SocketVoiceRecordOnclickListener() {
                        @Override
                        public void OnClicked(View view) {
                            popWindowUtils.showPop(mTvText);
                        }
                    });
                }
            },mTvText,R.layout.pop_socket_record_voice,true);
        }else {
            if (null!=socketRecordVoiceView){
                socketRecordVoiceView.setinit();
            }
        }
        popWindowUtils.showPop(mTvText);
    }
}
