package com.ericyl.themedemo.util;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by liangyu on 15/5/27.
 */
public class WGS84TOGCJ02 {

    private static final double a = 6378245.0;
    private static final double ee = 0.00669342162296594323;
    private static final double pi = 3.14159265358979324;

    private static boolean outOfChina(double lat, double lng) {
        if (lng < 72.004 || lng > 137.8347)
            return true;

        if (lat < 0.8293 || lat > 55.8271)
            return true;

        return false;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(x > 0 ? x : -x);
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;

        return ret;
    }

    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(x > 0 ? x : -x);
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;

        return ret;
    }

    /**
     * 标准坐标-》中国坐标
     */
    public static LatLng transformFromWGSToGCJ(LatLng wgLoc) {

        if (outOfChina(wgLoc.latitude, wgLoc.longitude)) {
            return wgLoc;
        }
        double dLat = transformLat(wgLoc.longitude - 105.0, wgLoc.latitude - 35.0);
        double dLon = transformLon(wgLoc.longitude - 105.0, wgLoc.latitude - 35.0);
        double radLat = wgLoc.latitude / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);

        LatLng mgLoc = new LatLng(wgLoc.latitude + dLat, wgLoc.longitude + dLon);
        return mgLoc;
    }

}
