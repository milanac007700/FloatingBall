package com.milanac007.demo.floatingball;

import android.content.Context;
import android.util.DisplayMetrics;

public class Utils {
    public static int dp2dx(Context context, float v) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        float scale = dm.density;
        return (int)(scale * v + 0.5f);
    }
}
