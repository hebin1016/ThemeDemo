package com.ericyl.themedemo.util;

import android.content.Context;

import com.ericyl.themedemo.ThemeDemoApplication;

/**
 * Created by liangyu on 15/5/20.
 */
public class AppProperties {

    public static Context getContext() {
        return ThemeDemoApplication.getApplication();
    }

}
