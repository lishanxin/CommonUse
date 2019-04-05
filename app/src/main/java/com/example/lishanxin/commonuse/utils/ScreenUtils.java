package com.example.lishanxin.commonuse.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenUtils {
    public static int getScreenWidth(Context context){
        return getDisplayMetrics(context).widthPixels;
    }

    public static int getScreenHeight(Context context){
        return getDisplayMetrics(context).heightPixels;
    }
    private static DisplayMetrics getDisplayMetrics(Context context){
        return context.getResources().getDisplayMetrics();
    }
}
