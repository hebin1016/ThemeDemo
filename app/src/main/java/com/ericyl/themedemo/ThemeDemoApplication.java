package com.ericyl.themedemo;

import android.app.Application;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

/**
 * Created by liangyu on 15/4/5.
 */
public class ThemeDemoApplication extends Application {

    private static ThemeDemoApplication applicationContext;

    public static ThemeDemoApplication getApplication() {
        return applicationContext;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        applicationContext = this;
    }

}
