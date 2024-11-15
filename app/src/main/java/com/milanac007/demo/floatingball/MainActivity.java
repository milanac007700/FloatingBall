package com.milanac007.demo.floatingball;

import android.accessibilityservice.AccessibilityService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity  {
    private static final String TAG = "MainActivity";
    private Context mContext;
    private int maxHeight;
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        customStatusBar();
    }

    protected void customStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setColor(this, getColor(R.color.purple_500), 0);
            StatusBarUtil.setDarkMode(this);
            updateNeedOffsetViewLayout();
        }
    }

    private int needOffsetViewID = -1;
    protected int getNeedOffsetViewLayout() {
        return -1;
    }

    protected void updateNeedOffsetViewLayout() {
        needOffsetViewID = getNeedOffsetViewLayout();
        if(needOffsetViewID != -1) {
            View needOffsetView = findViewById(needOffsetViewID);
            if (needOffsetView != null) {
                Object haveSetOffset = needOffsetView.getTag(StatusBarUtil.TAG_KEY_HAVE_SET_OFFSET);
                if (haveSetOffset != null && (Boolean) haveSetOffset) {
                    return;
                }else {
                    ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) needOffsetView.getLayoutParams();
                    layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + StatusBarUtil.getStatusBarHeight(mContext),
                            layoutParams.rightMargin, layoutParams.bottomMargin);
                    needOffsetView.setTag(StatusBarUtil.TAG_KEY_HAVE_SET_OFFSET, true);
                }
            }
        }
    }

    private boolean isLackOverLayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return !Settings.canDrawOverlays(mContext);
        }
        return true;
    }

    public boolean isAccessibilitySettingsOn(Context context, Class<? extends AccessibilityService> clazz) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName() + "/" + clazz.getCanonicalName();
        ContentResolver contentResolver = context.getApplicationContext().getContentResolver();
        try {
            accessibilityEnabled = Settings.Secure.getInt(contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED);
        }catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');
        if(accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if(settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while(mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();
                    if(accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);


        findViewById(R.id.startBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isLackOverLayPermission()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else if(!isAccessibilitySettingsOn(mContext, MyAccessibilityService.class)) {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS); //需要在设置页手动开启服务
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }else {
                    startService(new Intent(mContext, FloatingService.class));
                    startService(new Intent(mContext, MyAccessibilityService.class));
                }
            }
        });

//        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

//        Rect rect = new Rect();
//        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//        Log.i(TAG, "rect.top: " + rect.top + ", rect.bottom: " + rect.bottom);
//
//        /**
//         * SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN:全面屏，显示区域和状态栏都位于屏幕的(0,0)点，rect.bottom代表内容区域的底部，不管有没有导航栏。故最大高度 = rect.bottom
//         * 非全面屏时，显示区域位于状态栏的下面，故最大垂直高度还应再减去状态栏高度
//         * 注：此种方式为没有软键盘弹起的情况。否则还应再减去软键盘的高度
//         */
//        if ((getWindow().getDecorView().getWindowSystemUiVisibility() & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) == View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN){
//            maxHeight = rect.bottom;
//        }else {
//            maxHeight = rect.bottom - getStatusBarHeight(this);
//        }
//        Log.i(TAG, "maxHeight: " + maxHeight);

        if(isLackOverLayPermission()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else if(!isAccessibilitySettingsOn(mContext, MyAccessibilityService.class)) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS); //需要在设置页手动开启服务
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else {
            startService(new Intent(mContext, FloatingService.class));
            startService(new Intent(mContext, MyAccessibilityService.class));
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        //返回桌面
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}