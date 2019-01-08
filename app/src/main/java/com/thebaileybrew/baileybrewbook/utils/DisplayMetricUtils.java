package com.thebaileybrew.baileybrewbook.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

public class DisplayMetricUtils {
    private static final String TAG = DisplayMetricUtils.class.getSimpleName();

    public static int displayToPixel(Context context, float dp) {
        final int scale = (int)context.getResources().getDisplayMetrics().density;
        Log.e(TAG, "displayToPixel: current density is: " + scale);
        return (int)((int) dp * scale + 0.5f);
    }

    public static float screenToPixel(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public int getScreenWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int determineScrollPadding(Context context, Boolean twoPane) {
        int padding = 0;
        int currentWidth = 0;
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if(twoPane) {
            currentWidth = displayMetrics.widthPixels;
            switch(currentWidth) {
                case 1536:
                    padding = 40;
                    break;
                case 1440:
                default:
                    padding = 50;
                    break;
                case 1080:
                    padding = 45;
                    break;
            }
        }
        return padding;
    }
}
