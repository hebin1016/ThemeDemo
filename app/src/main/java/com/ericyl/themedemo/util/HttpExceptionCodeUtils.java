package com.ericyl.themedemo.util;

import com.ericyl.themedemo.R;

/**
 * Created by liangyu on 15/5/20.
 */
public class HttpExceptionCodeUtils {

    public static final int SERVICE_CONNECTION_ERROR = 0;

    public static String getExceptionMessage(int errorCode){
        String message = null;
        switch (errorCode) {
            case SERVICE_CONNECTION_ERROR:
                message = AppProperties.getContext().getResources().getString(R.string.service_connection_error);
                break;
            default:
                message = AppProperties.getContext().getResources().getString(R.string.an_unknown_error_occurred);
                break;
        }
        return message;
    }

}
