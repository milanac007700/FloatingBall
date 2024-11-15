package com.milanac007.demo.floatingball;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.annotation.Nullable;

class FloatingBall extends ViewGroup {
    private static final String TAG = "FloatingBall";
    private Paint mPaint;

    private Point mStartPoint = new Point();
    private int maxWidth;
    private int maxHeight;
    private Context mContext;
    private OperationLayout operationLayout;
    private Rect savedRect = null;
    private String mType = "init";
    private WindowManager wm;

    public FloatingBall(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingBall(Context context) {
        this(context, null, 0);
    }

    public FloatingBall(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);


        //如果View本身不需要绘制功能，则设true会开启系统优化；View默认设为false，关闭了该标志；而ViewGroup及其子类，本身不具有绘制功能，默认设为true；
        this.setWillNotDraw(false);//如果需要绘制功能，则应显示关闭该标志位

//        设置BackgroundColor/Background 也会关闭上述标志位：
        this.setBackgroundColor(Color.TRANSPARENT);


        operationLayout = new OperationLayout(context, mCallback);
//        operationLayout.setWillNotDraw(false);
        addView(operationLayout);


        //方式二：
        post(new Runnable() {
            @Override
            public void run() {

                statusBarHeight = getStatusBarHeight(context);
                maxWidth = getDisplay().getWidth();
//                maxHeight = getDisplay().getHeight() - statusBarHeight - getNavigationBarHeight(context);
                maxHeight = getDisplay().getHeight() - statusBarHeight;

                Log.i(TAG, "screenWidth: " + getDisplay().getWidth() + ", screenHeight: " + getDisplay().getHeight() + ", getStatusBarHeight: " + getStatusBarHeight(context) + ", getNavigationBarHeight: " + getNavigationBarHeight(context));
                Log.i(TAG, "maxWidth: " + maxWidth + ", maxHeight: " + maxHeight);

                //初始化，保证垂直方向的operationLayout的显示
                WindowManager.LayoutParams lps = (WindowManager.LayoutParams)getLayoutParams();
                lps.y = operationLayout.getMeasuredHeight()/2 - Utils.dp2dx(mContext, 50)/2;
                lps.x = 0;
                wm.updateViewLayout(FloatingBall.this, lps);

                operationLayout.setVisibility(GONE);
            }
        });


        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick open");
                mCallback.onClick("open");
            }
        });
    }


    private OperationLayout.Callback mCallback = new OperationLayout.Callback() {
        @Override
        public void onClick(String type) {
            mType = type;
            switch (type) {
                case "open":{
                    WindowManager.LayoutParams lps = (WindowManager.LayoutParams)getLayoutParams();
                    savedRect = new Rect(lps.x, lps.y, lps.x + getWidth(), lps.y + getHeight());
                    Log.i(TAG, "savedRect: " + savedRect);

                    operationLayout.setVisibility(VISIBLE);
                    FloatingBall.this.setVisibility(GONE);

                    Animation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(1000);
                    startAnimation(animation);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FloatingBall.this.setVisibility(VISIBLE);

                            lps.width = operationLayout.getMeasuredWidth();
                            lps.height = operationLayout.getMeasuredHeight();
                            lps.y = savedRect.top + Utils.dp2dx(mContext, 50)/ 2 - operationLayout.getMeasuredHeight()/2;

                            if(savedRect.left == 0) {
                                lps.x = savedRect.left;
                            }else {
                                lps.x = savedRect.right - operationLayout.getMeasuredWidth();
                            }
                            wm.updateViewLayout(FloatingBall.this, lps);
                        }
                    }, 0);

                }break;
                case "close":{
                    operationLayout.setVisibility(GONE);
                    FloatingBall.this.setVisibility(GONE);

                    Animation animation = new AlphaAnimation(0, 1);
                    animation.setDuration(1000);
                    startAnimation(animation);

                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            FloatingBall.this.setVisibility(VISIBLE);

                            WindowManager.LayoutParams lps = (WindowManager.LayoutParams)getLayoutParams();
                            lps.width = savedRect.right - savedRect.left;
                            lps.height = savedRect.bottom - savedRect.top;
                            lps.y = savedRect.top;
                            lps.x = savedRect.left;
                            wm.updateViewLayout(FloatingBall.this, lps);
                        }
                    }, 0);

                }break;
                case "home":{
                    mCallback.onClick("close");

                    Intent intent = new Intent("android.accessibilityservice.AccessibilityService");
                    intent.putExtra("action_type", MyAccessibilityService.ACTION_HOME);
                    mContext.sendBroadcast(intent);
                }break;
                case "recent":{

                    mCallback.onClick("close");

                    Intent intent = new Intent("android.accessibilityservice.AccessibilityService");
                    intent.putExtra("action_type", MyAccessibilityService.ACTION_RECENTS);
                    mContext.sendBroadcast(intent);
                }break;
                case "lock":{
                    mCallback.onClick("close");

                    Intent intent = new Intent("android.accessibilityservice.AccessibilityService");
                    intent.putExtra("action_type", MyAccessibilityService.ACTION_LOCK_SCREEN);
                    mContext.sendBroadcast(intent);
                }break;
                case "screenShot":{
                    mCallback.onClick("close");

                    Intent intent = new Intent("android.accessibilityservice.AccessibilityService");
                    intent.putExtra("action_type", MyAccessibilityService.ACTION_TAKE_SCREENSHOT);
                    mContext.sendBroadcast(intent);
                }break;
                case "back":{
                    mCallback.onClick("close");

                    //方式一：
                    Intent intent = new Intent("android.accessibilityservice.AccessibilityService");
                    intent.putExtra("action_type", MyAccessibilityService.ACTION_BACK);
                    mContext.sendBroadcast(intent);

                    //方式二：
//                    new Thread() {
//                        @Override
//                        public void run() {
//                            try {
//                                Runtime runtime = Runtime.getRuntime();
//                                runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }.start();
                }break;
            }
        }
    };

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.i(TAG, "onLayout");
        switch (mType) {
            case "init":{
                Log.i(TAG, "floatingBall.getMeasuredWidth(): " + this.getMeasuredWidth() + ", floatingBall.getMeasuredHeight(): " + this.getMeasuredHeight());
                Log.i(TAG, "operationLayout.getMeasuredWidth(): " + operationLayout.getMeasuredWidth() + ", operationLayout.getMeasuredHeight(): " + operationLayout.getMeasuredHeight());
            }break;
            case "open":{
                int opLayoutWidth = operationLayout.getMeasuredWidth();
                int opLayoutHeight = operationLayout.getMeasuredHeight();
                Log.i(TAG, "onLayout open, getMeasuredHeight:" + getMeasuredHeight());

                if(savedRect.left == 0){
                    operationLayout.setRight(false);
                }else {
                    operationLayout.setRight(true);
                }
                operationLayout.layout(0, 0, opLayoutWidth, opLayoutHeight);
            }break;
            case "close":{
                Log.i(TAG, "onLayout close, getMeasuredHeight:" + getMeasuredHeight());
            }break;
        }
    }


    /**
     * 状态栏高度
     */
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure");

        //测量children： measureChildren -> measureChild -> child.measure -> child.onMeasure
        this.measureChildren(widthMeasureSpec, heightMeasureSpec);

        //测量自己
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if(mType.equalsIgnoreCase("init") || mType.equalsIgnoreCase("close")){//设置其他状态时的大小
            if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(Utils.dp2dx(mContext, 50), Utils.dp2dx(mContext, 50));
            }
            else if(widthMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(Utils.dp2dx(mContext, 50), heightSize);
            }
            else if(heightMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(widthSize, Utils.dp2dx(mContext, 50));
            }

        }else if(mType.equalsIgnoreCase("open")){ //展开时：大小等于operationLayout大小
            if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(operationLayout.getMeasuredWidth(), operationLayout.getMeasuredHeight());
            }
            else if(widthMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(operationLayout.getMeasuredWidth(), heightSize);
            }
            else if(heightMode == MeasureSpec.AT_MOST) {
                setMeasuredDimension(widthSize, operationLayout.getMeasuredHeight());
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "onDraw");

        Log.i(TAG, "onDraw,mType: " + mType + ", getMeasuredWidth(): " + getMeasuredWidth() + ", getMeasuredHeight(): " + getMeasuredHeight());

        //初始状态和关闭状态时，绘制悬浮球
        if(mType.equalsIgnoreCase("init") || mType.equalsIgnoreCase("close")){

            //绘制边界区域
//            mPaint.setColor(Color.BLACK);
//            mPaint.setStrokeWidth(2);
//            mPaint.setStyle(Paint.Style.STROKE);//空心圆
//            canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);

            // measure->layout->draw, 绘制的区域不能超过layout确定的区域(mLeft、mTop、mRight、mBottom)，cx、cy都是相对于layout确定的布局的左上角(mLeft、mTop)的偏移。
            mPaint.setColor(Color.LTGRAY);
            mPaint.setAlpha(0x40); //只改变当前画笔的透明度
            mPaint.setStyle(Paint.Style.FILL);
            float cx = getWidth()/2.0f ;
            float cy =  getHeight()/2.0f;
            float r = Math.min(getWidth(), getHeight())/2.0f;
            canvas.drawCircle(cx, cy, r, mPaint);

            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeWidth(5);
            mPaint.setStyle(Paint.Style.STROKE);//空心圆
            float r1 = Math.min(getWidth(), getHeight())/4.0f;
            canvas.drawCircle(cx, cy, r1, mPaint);

//            this.setAlpha(0.5f); //设置其及其上所有内容的透明度
        }
    }

    private int statusBarHeight;
    private boolean isDrag = false;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i(TAG, "onTouchEvent");
        boolean result = false;
        if(isEnabled()) {
            result = true;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:{
                    super.onTouchEvent(event); //保证原有逻辑，例如onClick
                    mStartPoint.x = (int)event.getRawX();
                    mStartPoint.y = statusBarHeight + (int)event.getY(); //保存按下时的event.getY()，因为拖动时event.getY()是变化的
                    isDrag = false;
                    System.out.println("@@@ statusBarHeight: " + statusBarHeight +  " ,event.getX(): " + (int)event.getX() + " ,event.getY(): " + (int)event.getY() + " ,event.getRawX(): " + (int)event.getRawX() + ",event.getRawY(): " + (int)event.getRawY());
                    System.out.println("@@@ mStartPoint.x: " + mStartPoint.x + ", mStartPoint.y: " + mStartPoint.y);
                }break;
                case MotionEvent.ACTION_MOVE:{
                    int offsetX = (int)event.getRawX() - mStartPoint.x;
                    if (mStartPoint.x >= maxWidth - getWidth()) {
                        offsetX = (int)event.getRawX();
                    }
                    int offsetY = (int)event.getRawY() - mStartPoint.y; //正确, 布局的原点在状态栏下面的左上角，所以需要减去状态栏的高度。

                    /**
                     * getRawX()和getRawY()获得的是相对屏幕的位置，
                     * getX()和getY()是相对view的触摸位置坐标。可能为负数。移到View上方时，getY()返回负数，移到View下方时，getY()将返回的值大于getHeight()，getX()也是类似的。
                     *
                     * 所以下面注释的代码是错误的：无论是向上拖动还是向下拖动，event.getRawY() - event.getY() 的差值固定，使offsetY保存不变。
                     * // int offsetY = (int)(event.getRawY() - event.getY() - statusBarHeight);
                     *
                     */

                    System.out.println("@@@ event.getX(): "  +(int)event.getX() +  " ,event.getY(): " + (int)event.getY() + " ,event.getRawX(): " + (int)event.getRawX() + " ,event.getRawY(): " + (int)event.getRawY());
                    System.out.println("@@@ offsetX: " + offsetX + ", offsetY: " + offsetY);

                    final int minSize = 3;
                    if(Math.abs(offsetX) >= minSize || Math.abs(offsetY) >= minSize) {
                        isDrag = true;
                        WindowManager.LayoutParams lps = (WindowManager.LayoutParams)getLayoutParams();
                        lps.x = offsetX;
                        lps.y = offsetY;
                        wm.updateViewLayout(FloatingBall.this, lps);
                    }
                } break;
                case MotionEvent.ACTION_UP:{
                    if(!isDrag) { //保证原有逻辑，例如onClick
                        super.onTouchEvent(event);
                    }else {
                        WindowManager.LayoutParams lps = (WindowManager.LayoutParams)getLayoutParams();

                        //左右靠边
                        if(lps.x + getWidth()/2 < maxWidth/2) {
                            lps.x = 0;
                        }else {
                            lps.x = maxWidth - getWidth();
                        }

                        //保证垂直方向的operationLayout的显示
                        int opLayoutHeight = getHeight() * 5; //operationLayout的measuredHeight
                        if(lps.y + getHeight()/2 < opLayoutHeight/2){
                            lps.y = opLayoutHeight/2 - getHeight()/2;
                        }

                        if(lps.y + getHeight()/2 + opLayoutHeight/2 > maxHeight){
                            lps.y = maxHeight - opLayoutHeight/2 - getHeight()/2;
                        }

                        wm.updateViewLayout(FloatingBall.this, lps);
                    }
                }break;
            }
        }
        return result;
    }

}

