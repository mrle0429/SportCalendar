package com.test.sport.utils;

import android.content.Context;
import android.widget.Toast;

// TODO: 2024/9/5  Toast
public class ToastUtils {

    public static Toast mToast;

    private static ToastUtils toastUtils;

    public static ToastUtils getInstance() {
        if (toastUtils == null) {
            toastUtils = new ToastUtils();
        }
        return toastUtils;
    }

    /**
     * 传入文字
     */
    public void show(Context context, String text) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

}
