package com.milanac007.demo.floatingball;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FloatingService extends Service {
    public FloatingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        FloatingManager.getInstance(this).dismissFloatingBall();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FloatingManager.getInstance(this).showFloatingBall(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}