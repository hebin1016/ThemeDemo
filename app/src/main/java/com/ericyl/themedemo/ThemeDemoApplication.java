package com.ericyl.themedemo;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;

/**
 * Created by liangyu on 15/4/5.
 */
public class ThemeDemoApplication extends Application {

    private static ThemeDemoApplication applicationContext;
    private static GoogleAnalytics analytics;
    private static Tracker tracker;

    public static ThemeDemoApplication getApplication() {
        return applicationContext;
    }

    public static GoogleAnalytics analytics() {
        return analytics;
    }

    public static Tracker tracker() {
        return tracker;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        applicationContext = this;

        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-63483022-1");
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
    }

}
