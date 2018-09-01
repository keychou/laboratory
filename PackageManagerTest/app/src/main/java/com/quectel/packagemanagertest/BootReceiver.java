package com.quectel.packagemanagertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by klein on 18-8-29.
 */

public class BootReceiver extends BroadcastReceiver {
    private final String TAG = "PackageManagerTest";
    @Override
    public void onReceive(Context context, Intent intent){
        //接收安装广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString();
            Log.d(TAG, "安装了:" +packageName + "包名的程序");
        }
        //接收卸载广播
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            String packageName = intent.getDataString();
            Log.d(TAG, "卸载了:"  + packageName + "包名的程序");

        }
    }
}