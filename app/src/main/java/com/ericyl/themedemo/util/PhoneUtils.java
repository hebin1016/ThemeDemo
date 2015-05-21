package com.ericyl.themedemo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.ericyl.themedemo.R;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by liangyu on 15/4/5.
 */
public class PhoneUtils {

    public static String getNativePhoneNumber() {
        TelephonyManager telephonyManager = (TelephonyManager) AppProperties.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String NativePhoneNumber = null;
        NativePhoneNumber = telephonyManager.getLine1Number();
        return NativePhoneNumber;
    }

    public static String getProvidersName() {
        TelephonyManager telephonyManager = (TelephonyManager) AppProperties.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String ProvidersName = null;
        String IMSI = telephonyManager.getSubscriberId();
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            ProvidersName = AppProperties.getContext().getString(R.string.cmcc);
        } else if (IMSI.startsWith("46001")) {
            ProvidersName = AppProperties.getContext().getString(R.string.china_unicom);
        } else if (IMSI.startsWith("46003")) {
            ProvidersName = AppProperties.getContext().getString(R.string.china_telecom);
        }
        return ProvidersName;
    }

    public static String getSimCode() {
        TelephonyManager telephonyManager = (TelephonyManager) AppProperties.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSubscriberId();
    }

    public static UUID getUUID() {
        UUID uuid = null;
        try {
            final String androidID = Settings.Secure.getString(
                    AppProperties.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            if (!"9774d56d682e549c".equals(androidID)) {
                uuid = UUID.nameUUIDFromBytes(androidID.getBytes("utf8"));
            } else {
                final String deviceId = ((TelephonyManager) AppProperties.getContext()
                        .getSystemService(Context.TELEPHONY_SERVICE))
                        .getDeviceId();
                uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId
                        .getBytes("utf8")) : UUID.randomUUID();
            }
            return uuid;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) AppProperties.getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    Log.v("network", i + "===Status===" + networkInfo[i].getState()
                            + "===Type===" + networkInfo[i].getTypeName());
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
