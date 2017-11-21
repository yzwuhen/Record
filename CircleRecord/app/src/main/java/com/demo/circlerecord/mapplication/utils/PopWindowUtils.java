package com.demo.circlerecord.mapplication.utils;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;


public class PopWindowUtils {
    private static PopWindowUtils pop;
    private Context context;

    private PopupWindow popWindow;
    private TextView tvTrue;

    private boolean windowClick = true;
    private onWindownListener onWindownListener;


    public static PopWindowUtils getInstance() {
        if (pop == null) {
            pop = new PopWindowUtils();
        }
        return pop;
    }

    public PopupWindow general(Context context, onViewListener listen, final View view, int layoutId, boolean b) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopWindow = inflater.inflate(layoutId, null,
                false);
        popWindow = new PopupWindow(vPopWindow,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, false);
        listen.getView(popWindow, vPopWindow, view);
        //popWindow.setAnimationStyle(R.style.popwin_anim_style);
        // 在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 点击空白处时，隐藏掉pop窗口
        popWindow.setFocusable(b);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        backgroundAlpha(150, (Activity) (context));
        // 添加pop窗口关闭事件
        popWindow.setOnDismissListener(new poponDismissListener());

        vPopWindow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (windowClick) {
                    popWindow.dismiss();
                    if (mViewCallBackListener!=null){
                        mViewCallBackListener.viewCallBcak();
                    }
                }


            }
        });
        return popWindow;

    }

    /**
     * 公用选择器 传入布局 返回pop//通用
     */
    public PopupWindow general(boolean b,Context context, onViewListener listen, final View view, int layoutId) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopWindow = inflater.inflate(layoutId, null,
                false);
        popWindow = new PopupWindow(vPopWindow,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, false);
        listen.getView(popWindow, vPopWindow, view);
        //popWindow.setAnimationStyle(R.style.popwin_anim_style);
        // 在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 点击空白处时，隐藏掉pop窗口
        popWindow.setFocusable(true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());

        // 添加pop窗口关闭事件
        if (b){
            backgroundAlpha(150, (Activity) (context));
        }
        popWindow.setOnDismissListener(new poponDismissListener());

        vPopWindow.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (windowClick) {
                    if (onWindownListener!=null){
                        onWindownListener.windownsCallback();
                    }
                    popWindow.dismiss();
                }

            }
        });

        return popWindow;
    }


    /**
     * 公用选择器 传入布局 返回pop//通用
     */
    public PopupWindow general(boolean popclose,Context context,
                               onViewListener listen, final View view, int layoutId,boolean isHideWindow) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vPopWindow = inflater.inflate(layoutId, null,
                false);
        popWindow = new PopupWindow(vPopWindow,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT, false);
        listen.getView(popWindow, vPopWindow, view);
        //popWindow.setAnimationStyle(R.style.popwin_anim_style);
        // 在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        popWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popWindow
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        // 点击空白处时，隐藏掉pop窗口
        popWindow.setFocusable(isHideWindow);
        popWindow.setBackgroundDrawable(new BitmapDrawable());

        // 添加pop窗口关闭事件
        if (popclose){
            backgroundAlpha(150, (Activity) (context));
        }
        if (isHideWindow){
            popWindow.setOnDismissListener(new poponDismissListener());

            vPopWindow.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    if (windowClick) {
                        if (onWindownListener!=null){
                            onWindownListener.windownsCallback();
                        }
                        popWindow.dismiss();
                    }


                }
            });
        }

        return popWindow;
    }


    public void showPop(View parent) {
        if (popWindow != null) {
            if (!popWindow.isShowing()) {
                popWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            } else {
                popWindow.dismiss();
            }
        }
    }

    public void hidePop() {
        if (popWindow != null) {
            if (!popWindow.isShowing()) {
            } else {
                popWindow.dismiss();
            }
        }
    }


    public void showPop(View parent, int gravity) {
        if (popWindow != null) {
            if (!popWindow.isShowing()) {

                popWindow.showAtLocation(parent, gravity, 0, 0);
            } else {
                popWindow.dismiss();
            }
        }


    }

    public void setWindowsListener(onWindownListener onWindownListener) {
        this.onWindownListener =onWindownListener;

    }

    /**
     * 返回View对象
     *
     * @author shishuyao
     */
    public interface onViewListener {
        void getView(PopupWindow pop, View view, View parent);
    }

    public interface onWindownListener{
        void windownsCallback();
    }

    /**
     * 返回View对象
     *
     * @author
     */
    public interface onPopListener {
        void getpopView(PopupWindow pop, View view, View parent);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha, Activity activity) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 添加新笔记时弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     *
     * @author cg
     */
    class poponDismissListener implements PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            // TODO Auto-generated method stub
            // Log.v("List_noteTypeActivity:", "我是关闭事件");
            backgroundAlpha(1f, (Activity) context);
        }
    }

    public void setClick(boolean b) {
        // TODO Auto-generated method stub
        this.windowClick = b;
    }

    private ViewCallBackListener mViewCallBackListener;
    public void setViewBcakListener(ViewCallBackListener viewCallBackListener){
        this.mViewCallBackListener =viewCallBackListener;
    }

    public  interface ViewCallBackListener{
        void viewCallBcak();
    }
}
