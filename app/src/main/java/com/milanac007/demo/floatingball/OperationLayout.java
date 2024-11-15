package com.milanac007.demo.floatingball;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OperationLayout extends ViewGroup {
    private static final String TAG = "OperationLayout";
    private Context mContext;
    private Callback mCallback;
    private boolean isRight = false;
    Paint mPaint;

    public interface Callback {
         void onClick(String type);
    }

    public OperationLayout(@NonNull Context context){
        this(context, null, 0);
    }

    public OperationLayout(@NonNull Context context, Callback callback){
        this(context, null, 0);
        mCallback = callback;
    }


    public OperationLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OperationLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        for(int i = 0; i <= 5; i++) {
            CircleView child = new CircleView(context);
            child.setId(i);
            Drawable bg = context.getResources().getDrawable(R.mipmap.icon_circle);
            bg.setAlpha(0x40);
            child.setBackground(bg);

            int srcId = -1;
            switch (i) {
                case 0: {
                    srcId = R.mipmap.icon_rect;
                }break;
                case 1: {
                    srcId = R.mipmap.icon_menu;
                }break;
                case 2: {
                    srcId = R.mipmap.icon_lock;
                }break;
                case 3: {
                    srcId = R.mipmap.icon_cut;
                }break;
                case 4: {
                    srcId = R.mipmap.icon_back;
                }break;
                case 5:{
                    srcId = R.mipmap.icon_del;
                }break;
            }
            child.setScaleType(ImageView.ScaleType.CENTER);
            child.setImageDrawable(context.getResources().getDrawable(srcId));
            child.setOnClickListener(onClickListener);
            addView(child, i);
        }

    }

    /**
     * 消费事件，此时父控件FloatingBall区域=operationLayout大小，如果不消费事件，则会进入父控件FloatingBall的onTouchEvent方法
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mCallback != null) {
            mCallback.onClick("close");
        }
        return true;
    }

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case 0:{
                    if(mCallback != null) {
                        mCallback.onClick("home");
                    }
                }break;
                case 1:{
                    if(mCallback != null) {
                        mCallback.onClick("recent");
                    }
                }break;
                case 2:{
                    if(mCallback != null) {
                        mCallback.onClick("lock");
                    }
                }break;
                case 3:{
                    if(mCallback != null) {
                        mCallback.onClick("screenShot");
                    }
                }break;
                case 4:{
                    if(mCallback != null) {
                        mCallback.onClick("back");
                    }
                }break;
                case 5:{
                    if(mCallback != null) {
                        mCallback.onClick("close");
                    }
                }break;
            }
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure");
        this.measureChildren(widthMeasureSpec, heightMeasureSpec);

        int childWidth = getChildAt(0).getMeasuredWidth();
        int childHeight = getChildAt(0).getMeasuredHeight();

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(3 * childWidth, 5 * childHeight);
        }
        else if(widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(3 * childWidth, heightSize);
        }
        else if(heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, 5 * childHeight);
        }

    }

    public void setRight(boolean isRight){
        this.isRight = isRight;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.i(TAG, "onLayout");
        int childWidth = getChildAt(0).getMeasuredWidth();
        int childHeight = getChildAt(0).getMeasuredHeight();

        if(!isRight) {
            int childLeft = 0, childTop = 0;
            getChildAt(0).layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            childLeft = (int)(Math.sqrt(2.0) * childWidth);
            childTop = getMeasuredHeight()/2 - (int)(Math.sqrt(2.0) * childHeight + childHeight/2);
            getChildAt(1).layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            childLeft = childWidth * 2;
            childTop = childHeight * 2;
            getChildAt(2).layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            childLeft = (int)(Math.sqrt(2.0) * childWidth);
            childTop = getMeasuredHeight()/2 + (int)(Math.sqrt(2.0) * childHeight - childHeight/2);
            getChildAt(3).layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            childLeft = 0;
            childTop = childHeight * 4;
            getChildAt(4).layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            childLeft = 0;
            childTop = childHeight * 2;
            getChildAt(5).layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

        }else {
            int childLeft = getMeasuredWidth() - childWidth, childTop = 0;
            getChildAt(0).layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            childLeft = getMeasuredWidth() - (int)(Math.sqrt(2.0) * childWidth + childWidth);
            childTop = getMeasuredHeight()/2 - (int)(Math.sqrt(2.0) * childHeight + childHeight/2);
            getChildAt(1).layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            childLeft = 0;
            childTop = childHeight * 2;
            getChildAt(2).layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            childLeft = getMeasuredWidth() - (int)(Math.sqrt(2.0) * childWidth + childWidth);
            childTop = getMeasuredHeight()/2 + (int)(Math.sqrt(2.0) * childHeight - childHeight/2);
            getChildAt(3).layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            childLeft = getMeasuredWidth() - childWidth;
            childTop = childHeight * 4;
            getChildAt(4).layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            childLeft = getMeasuredWidth() - childWidth;
            childTop = childHeight * 2;
            getChildAt(5).layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas); //View的onDraw方法，空实现
        Log.i(TAG, "onDraw");

        //绘制边界区域
//        mPaint.setAlpha(0x40); //只修改当前画笔的透明度
//        this.setAlpha(0.5f); //其上的所有内容的透明度都改变

        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(2);
        mPaint.setStyle(Paint.Style.STROKE);//空心圆
        canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
    }

}

class CircleView extends ImageView {
    private Context mContext;
    private static final String TAG = "CircleView";
    public CircleView(@NonNull Context context){
        super(context);
        mContext = context;
    }
    public CircleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(Utils.dp2dx(mContext, 50), Utils.dp2dx(mContext, 50));
        }
        else if(widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(Utils.dp2dx(mContext, 50), heightSize);
        }
        else if(heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, Utils.dp2dx(mContext, 50));
        }
    }
}
