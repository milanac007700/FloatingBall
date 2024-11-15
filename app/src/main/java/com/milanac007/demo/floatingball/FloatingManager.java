package com.milanac007.demo.floatingball;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;

public class FloatingManager {
    private WindowManager.LayoutParams mLayoutParams;
    private WindowManager wm;
    private FloatingBall mFloatingBall;
    private static FloatingManager mInstance;
    private int maxWidth, maxHeight;

    public static FloatingManager getInstance(Context context) {
        if(mInstance == null) {
            synchronized (FloatingManager.class) {
                if(mInstance == null) {
                    mInstance = new FloatingManager(context);
                }
            }
        }
        return mInstance;
    }

    public FloatingManager(Context context) {
        wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        maxWidth = dm.widthPixels;
//        maxHeight = dm.heightPixels - statusBarHeight - getNavigationBarHeight(context);
        maxHeight = dm.heightPixels - getStatusBarHeight(context);
        Log.i("FloatingManager", "maxHeight = dm.heightPixels - statusBarHeight: " + maxHeight);
    }

    public void showFloatingBall(Context context) {
        if(mFloatingBall == null) {
            mFloatingBall = new FloatingBall(context);
            mLayoutParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, PixelFormat.TRANSPARENT);
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
//            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY; //android 8.0上悬浮窗会报错 直接闪退，设置window的类型就好了：
            mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            mLayoutParams.x = 0;
            mLayoutParams.y = 0;
            wm.addView(mFloatingBall, mLayoutParams);
        }
    }

    public void dismissFloatingBall() {
        if(mFloatingBall != null) {
            wm.removeView(mFloatingBall);
            mFloatingBall = null;
        }
    }

    private int getStatusBarHeight(Context context) {
        int id = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(id);
    }

    /**
     * 导航栏高度
     */
    private int getNavigationBarHeight(Context context) {
        int rid = context.getResources().getIdentifier("config_showNavigationBar", "bool", "android");
        if(rid != 0) {
            int id = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
            return context.getResources().getDimensionPixelSize(id);
        } else {
            return 0;
        }
    }
}
