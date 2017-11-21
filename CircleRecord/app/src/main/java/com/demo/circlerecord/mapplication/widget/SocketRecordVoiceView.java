package com.demo.circlerecord.mapplication.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.circlerecord.mapplication.R;
import com.demo.circlerecord.mapplication.utils.CommonUtils;
import com.demo.circlerecord.mapplication.utils.VoiceRecorder;

/**
 * Created by yz_wuhen on 2017/8/10.
 */

public class SocketRecordVoiceView extends LinearLayout implements View.OnClickListener {

    private ImageView ivBack;
    private ImageView recordingChang;
    private CircularProgressDrawable drawableChang;
    private ObjectAnimator currentAnimationChang;
    private TimeTextView tvOnlyTime,tvTouchTime;
    private ImageView cusRecordVoiceView;

    CircularProgressDrawable drawable;
    // 录音由状态转为录音状态
    private static final int SOCKET_RECORDING_TYPE_INIT = 2;
    ///暂停录音 等待播主决定发送还是取消
    private static final int SOCKET_RECORDING_TYPE_RECONDING = 3;
    //用户按下发送 录音 一切状态归init
    private static final int SOCKET_RECORDING_TYPE_SEND = 4;
    private int recording_type = SOCKET_RECORDING_TYPE_INIT;

    ObjectAnimator currentAnimation;
    private ImageView recording;
    private View recordingSend;
    private View recordingPause;


    protected Context mContext;;
    protected VoiceRecorder voiceRecorder;
    protected PowerManager.WakeLock wakeLock;

    private int length;

    //移动的左边点
    float eventStartY = 0;
    float eventEndY =0;
    View containerDan,containerChang;
    TextView mTvTouchExplain;
    protected Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            //可以在這回掉做動畫效果

        }
    };
    public SocketRecordVoiceView(Context context) {
        super(context);
        initView(context);
    }

    public SocketRecordVoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SocketRecordVoiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext =context;
        View.inflate(context, R.layout.pop_socket_select_click_record,this);
        containerDan =  findViewById(R.id.cantiner1);
        containerChang = findViewById(R.id.cantiner2);
        RadioGroup rg = (RadioGroup) findViewById(R.id.rg);
        mTvTouchExplain = (TextView) findViewById(R.id.tv_touch_explain);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rd_dan){
                    containerDan.setVisibility(View.VISIBLE);
                    containerChang.setVisibility(View.GONE);
                    mTvTouchExplain.setVisibility(GONE);
                }else if(checkedId == R.id.rd_chang){
                    containerDan.setVisibility(View.GONE);
                    containerChang.setVisibility(View.VISIBLE);

                }
            }
        });
        tvOnlyTime = (TimeTextView) findViewById(R.id.count_second);
        tvTouchTime = (TimeTextView)findViewById(R.id.count_second_chang);
        recordingSend = findViewById(R.id.send_recording);
        cusRecordVoiceView = (ImageView) findViewById(R.id.touch_recording);

        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.iv_back).setOnClickListener(this);
        recordingPause = findViewById(R.id.pause_recording);
        recording = (ImageView) findViewById(R.id.recording);
        recordingChang = (ImageView) findViewById(R.id.recording_chang);

        initRecording();
        initRecordingChang();
        currentAnimation = prepareStyle2Animation(drawable);
        currentAnimationChang = prepareStyle2Animation(drawableChang);
        findViewById(R.id.recording_container).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (recording_type == SOCKET_RECORDING_TYPE_INIT) {   // 录音由状态转为录音状态
                    if (currentAnimation != null) {
                        currentAnimation.cancel();
                    }
                    recording_type = SOCKET_RECORDING_TYPE_RECONDING;
                    length=0;
                    currentAnimation.start();
                    changeRecodingView(SOCKET_RECORDING_TYPE_INIT);
                    startRecording();  // 开启录音
                    tvOnlyTime.downTime();
                }else if (recording_type == SOCKET_RECORDING_TYPE_RECONDING){  ///暂停录音 等待播主决定发送还是取消
                    currentAnimation.cancel();  // 动画停止
                    changeRecodingView(SOCKET_RECORDING_TYPE_RECONDING);
                    recording_type = SOCKET_RECORDING_TYPE_SEND;
                    length = stopRecoding();
                    tvOnlyTime.stopTime();
                }else if (recording_type == SOCKET_RECORDING_TYPE_SEND){  //用户按下发送 录音 一切状态归init
                    changeRecodingView(SOCKET_RECORDING_TYPE_SEND);
                    recording_type = SOCKET_RECORDING_TYPE_INIT;
                    initAnimation();
                    sendRecording();    //发送录音
                    tvOnlyTime.cancleTime();
                }
            }
        });

        findViewById(R.id.recording_container2).setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        eventStartY=Math.abs(event.getY());
                        length=0;
                        try {
                            if (currentAnimationChang != null) {
                                currentAnimationChang.cancel();
                            }
                            startRecording();
                            tvTouchTime.downTime();
                            currentAnimationChang.start();
                        } catch (Exception e) {
                        }
                        mTvTouchExplain.setVisibility(VISIBLE);
                        mTvTouchExplain.setText("手指滑出此区域，取消发送");
                        mTvTouchExplain.setBackgroundColor(getResources().getColor(R.color.pur_708fcc));
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //宽度不大准 但允许误差
                        if (event.getY()<0||event.getY()>cusRecordVoiceView.getHeight()||event.getX()<20||event.getX()>cusRecordVoiceView.getWidth()){
                            mTvTouchExplain.setText("松开手指取消发送");
                            mTvTouchExplain.setBackgroundColor(getResources().getColor(R.color.red_F40808));
                        }else {
                            mTvTouchExplain.setText("手指滑出此区域，取消发送");
                            mTvTouchExplain.setBackgroundColor(getResources().getColor(R.color.pur_708fcc));
                        }
                        return true;
                    case MotionEvent.ACTION_UP:

                        mTvTouchExplain.setVisibility(GONE);
                        eventEndY=Math.abs(event.getY());
                        currentAnimationChang.cancel();  // 动画停止并回归初始状态
                        initAnimationChang();
                        length = stopRecoding();
                        tvTouchTime.stopTime();
                        if (event.getY()<0||event.getY()>cusRecordVoiceView.getHeight()||event.getX()<20||event.getX()>cusRecordVoiceView.getWidth()){
                        }else {
                            try {
                                //发送录音
                                sendRecording();
                                tvTouchTime.cancleTime();
                            } catch (Exception e) {
                                e.printStackTrace();
                                tvTouchTime.cancleTime();
                                Toast.makeText(mContext, R.string.send_failure_please, Toast.LENGTH_SHORT).show();
                            }
                        }

                        return true;
                    default:
                        discardRecording();
                        return false;
                }
            }
        });
        voiceRecorder = new VoiceRecorder(micImageHandler);
        wakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "voice");
    }

    //如果不是第一次加载了，再次进来的时候初始化状态，只需初始化点击按钮的状态
    public void setinit(){
        try {
            recording_type = SOCKET_RECORDING_TYPE_INIT;
            if (tvOnlyTime!=null){
                tvOnlyTime.stopTime();
                tvOnlyTime.cancleTime();
            }
            recordingSend.setVisibility(View.GONE);
            recordingPause.setVisibility(View.GONE);
            initAnimation();
        }catch (Exception e){

        }

    }
    private void initRecordingChang() {
        drawableChang = new CircularProgressDrawable.Builder()
                .setRingWidth(getResources().getDimensionPixelSize(R.dimen.dp_2))
                .setOutlineColor(getResources().getColor(R.color.light_purple))
                .setRingColor(getResources().getColor(R.color.light_purple))
                .setCenterColor(getResources().getColor(R.color.light_purple))
                .create();
        recordingChang.setImageDrawable(drawableChang);

    }

    private void initAnimation() {
        currentAnimation.setCurrentPlayTime(0);
        currentAnimation.cancel();
    }
    private void initAnimationChang() {
        currentAnimationChang.setCurrentPlayTime(0);
        currentAnimationChang.cancel();
    }


    private void initRecording() {
        drawable = new CircularProgressDrawable.Builder()
                .setRingWidth(getResources().getDimensionPixelSize(R.dimen.dp_2))
                .setOutlineColor(getResources().getColor(R.color.master_color))
                .setRingColor(getResources().getColor(R.color.master_color))
                .setCenterColor(getResources().getColor(R.color.master_color))
                .create();
        recording.setImageDrawable(drawable);
    }

    private void changeRecodingView(int recordingTypeInit) {
        switch (recordingTypeInit) {
            case SOCKET_RECORDING_TYPE_INIT:
                recordingSend.setVisibility(View.GONE);
                recordingPause.setVisibility(View.VISIBLE);
                break;
            case SOCKET_RECORDING_TYPE_RECONDING:
                recordingSend.setVisibility(View.VISIBLE);
                recordingPause.setVisibility(View.GONE);
                break;
            case SOCKET_RECORDING_TYPE_SEND:
                recordingSend.setVisibility(View.GONE);
                recordingPause.setVisibility(View.GONE);
              //  count_second.setText("点击说话");
                break;
        }

    }


    private ObjectAnimator prepareStyle2Animation(CircularProgressDrawable drawable) {

        ObjectAnimator progressAnimation = ObjectAnimator.ofFloat(drawable, CircularProgressDrawable.PROGRESS_PROPERTY,
                0f, 1f);
      //  progressAnimation.setRepeatCount(ObjectAnimator.RESTART);
        //设置动画效果的模式。
        progressAnimation.setRepeatMode(ObjectAnimator.RESTART);
        //设置动画效果的持续事件
        progressAnimation.setDuration(60000);
        //这个是插值器。详细的可以自行百度；
        progressAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        //动画监听
        progressAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                if (tvOnlyTime.getsTime()==6){
                    tvOnlyTime.stopTime();
                    tvOnlyTime.cancleTime();
                    length = stopRecoding();
                    initAnimation();
                    sendRecording();    //发送录音
                }
                if (tvTouchTime.getsTime()==6){
                    tvTouchTime.stopTime();
                    tvTouchTime.cancleTime();
                    currentAnimationChang.cancel();
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        return progressAnimation;
    }

    private void sendRecording() {
        if (length > 0) {
            if (recorderCallback != null) {
                recorderCallback.onVoiceRecordComplete(getVoiceFilePath(), length);
            }
        } else if (length == 401) {
            Toast.makeText(mContext, R.string.Recording_without_permission, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, R.string.The_recording_time_is_too_short, Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                if(recording_type == SOCKET_RECORDING_TYPE_RECONDING || recording_type == SOCKET_RECORDING_TYPE_SEND) { // 正在录音   // 录音完成  取消录音内容  状态归0
                    //录音取消   状态归0
                    initAnimation();
                    recording_type = SOCKET_RECORDING_TYPE_INIT;
                    changeRecodingView(SOCKET_RECORDING_TYPE_SEND);
                    length = 0;
                    tvOnlyTime.stopTime();
                    tvOnlyTime.cancleTime();
                  //  cancelRecording();   //取消录音内容
                }
                break;
            case R.id.iv_back:
                if (socketVoiceRecordOnclickListener!=null){
                    socketVoiceRecordOnclickListener.OnClicked(v);
                }
                break;
        }
    }


    public void startRecording() {
        if (!CommonUtils.isSdcardExist()) {
            Toast.makeText(mContext, R.string.Send_voice_need_sdcard_support, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            wakeLock.acquire();
            voiceRecorder.startRecording(mContext);
        } catch (Exception e) {
            e.printStackTrace();
            if (wakeLock.isHeld())
                wakeLock.release();
            if (voiceRecorder != null)
                voiceRecorder.discardRecording();
            Toast.makeText(mContext, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void discardRecording() {
        if (wakeLock.isHeld())
            wakeLock.release();
        try {
            // stop recording
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
            }
        } catch (Exception e) {
        }
    }
    public String getVoiceFilePath() {
        return voiceRecorder.getVoiceFilePath();
    }
    public int stopRecoding() {
        if (wakeLock.isHeld())
            wakeLock.release();
        return voiceRecorder.stopRecoding();
    }

    public void setBackOnClick(SocketVoiceRecordOnclickListener socketVoiceRecordOnclickListener) {
        this.socketVoiceRecordOnclickListener =socketVoiceRecordOnclickListener;

    }
    private SocketVoiceRecordOnclickListener socketVoiceRecordOnclickListener;
    public interface SocketVoiceRecordOnclickListener{
        void OnClicked(View view);
    }

    public interface SocketVoiceRecorderCallback {
        /**
         * on voice record complete
         *
         * @param voiceFilePath
         *            录音完毕后的文件路径
         * @param voiceTimeLength
         *            录音时长
         */
        void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength);
    }
    private SocketVoiceRecorderCallback recorderCallback;
    public void setSocketTouchCall( SocketVoiceRecorderCallback recorderCallbac){
        this.recorderCallback =recorderCallbac;
    }
}
