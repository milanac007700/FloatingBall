package com.milanac007.demo.floatingball;

import android.accessibilityservice.AccessibilityService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class MyAccessibilityService extends AccessibilityService {
    public static final int ACTION_BACK = 1;
    public static final int ACTION_HOME = 2;
    public static final int ACTION_RECENTS = 3;
    public static final int ACTION_LOCK_SCREEN = 8;
    public static final int ACTION_TAKE_SCREENSHOT = 9;

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(mReceiver, new IntentFilter("android.accessibilityservice.AccessibilityService"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mReceiver);
        }catch (Exception e) {

        }

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int actionType = intent.getIntExtra("action_type", 0);
            Log.i("MyAccessibilityService", "actionType: " + actionType);
            if(actionType == 1 || actionType == 2 || actionType == 3 || actionType == 8 || actionType == 9) {
                performAction(actionType);
            }
        }
    };

    private void performAction(int action){
        performGlobalAction(action);
    }

}
