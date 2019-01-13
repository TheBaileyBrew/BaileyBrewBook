package com.thebaileybrew.baileybrewbook.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

// --Commented out by Inspection START (1/11/2019 1:06 PM):
//class DisplayMetricUtils {
//    private static final String TAG = DisplayMetricUtils.class.getSimpleName();
//
//// --Commented out by Inspection START (1/11/2019 1:06 PM):
////    public static int displayToPixel(Context context, float dp) {
// --Commented out by Inspection START (1/11/2019 1:06 PM):
//////        final int scale = (int)context.getResources().getDisplayMetrics().density;
//////        Log.e(TAG, "displayToPixel: current density is: " + scale);
//////        return (int)((int) dp * scale + 0.5f);
//////    }
////// --Commented out by Inspection STOP (1/11/2019 1:06 PM)
// --Commented out by Inspection STOP (1/11/2019 1:06 PM)
//
//// --Commented out by Inspection START (1/11/2019 1:06 PM):
////    public static float screenToPixel(Resources resources, float sp) {
////        final float scale = resources.getDisplayMetrics().scaledDensity;
////        return sp * scale;
////    }
////
////    public int getScreenWidth(Context context) {
////        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//// --Commented out by Inspection STOP (1/11/2019 1:06 PM)
//        return displayMetrics.widthPixels;
//    }
//
//// --Commented out by Inspection START (1/11/2019 1:06 PM):
////    public static int determineScrollPadding(Context context, Boolean twoPane) {
////        int padding = 0;
////        int currentWidth = 0;
////        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
////        if(twoPane) {
////            currentWidth = displayMetrics.widthPixels;
////            switch(currentWidth) {
////                case 1536:
// --Commented out by Inspection STOP (1/11/2019 1:06 PM)
//                    padding = 40;
//                    break;
//                case 1440:
//                default:
//                    padding = 50;
//                    break;
//                case 1080:
//                    padding = 45;
//                    break;
//            }
//        }
//        return padding;
//    }
// --Commented out by Inspection STOP (1/11/2019 1:06 PM)
//}
