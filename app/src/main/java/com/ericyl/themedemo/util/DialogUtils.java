package com.ericyl.themedemo.util;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import com.ericyl.themedemo.R;

/**
 * Created by liangyu on 15/5/20.
 */
public class DialogUtils {

    public static Dialog showDialogWithOneBtn(Context context, String title,
                                              String msg) {
        Dialog dialog = null;
        Builder builder = new Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setNeutralButton(context.getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int which) {
                        dlg.cancel();
                    }
                });
        dialog = builder.create();
        dialog.show();

        return dialog;
    }


    public static Dialog showDialogWithOneBtn(Context context, String title,
                                              String msg, String btnStr,
                                              DialogInterface.OnClickListener btnListener) {
        Dialog dialog = null;
        Builder builder = new Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setNeutralButton(btnStr, btnListener);
        dialog = builder.create();
        dialog.show();

        return dialog;
    }

    public static Dialog showDialogWithTwoBtn(Context context, String title,
                                               String msg, String btnStr,
                                               DialogInterface.OnClickListener btnListener) {
        Dialog dialog = null;
        Builder builder = new Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(btnStr, btnListener);
        builder.setNegativeButton(context.getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                });
        dialog = builder.create();
        dialog.show();

        return dialog;
    }

    public static Dialog showDialogWithTwoBtn(Context context, String title,
                                               String msg, String okStr,
                                               DialogInterface.OnClickListener okListener, String cancelStr,
                                               DialogInterface.OnClickListener cancelListener) {
        Dialog dialog = null;
        Builder builder = new Builder(context);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(okStr, okListener);
        builder.setNegativeButton(cancelStr, cancelListener);
        dialog = builder.create();
        dialog.show();

        return dialog;
    }

}
