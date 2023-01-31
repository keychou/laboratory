package com.mango.bluetoothtest;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class APP extends Application {
    public static final String TAG = "mango-bluetoothtest-APP";
    private static final Handler sHandler = new Handler();
    private static Toast sToast; // 单例Toast,避免重复创建，显示时间过长

    @SuppressLint("ShowToast")
    @Override
    public void onCreate() {
        super.onCreate();
        sToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    public static void toast(String txt, int duration) {
        Log.d(TAG, "txt = " + txt + ", duration = " + duration);
        if (sToast == null){
            Log.e(TAG, "sToast is null");
            return;
        }
        sToast.setText(txt);
        sToast.setDuration(duration);
        sToast.show();
    }

    public static void runUi(Runnable runnable) {
        sHandler.post(runnable);
    }
}
