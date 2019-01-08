package com.thebaileybrew.baileybrewbook;

import android.app.Application;

public class BaileyBrewBook extends Application {

    private static BaileyBrewBook mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static BaileyBrewBook getContext() {
        return mContext;
    }
}
