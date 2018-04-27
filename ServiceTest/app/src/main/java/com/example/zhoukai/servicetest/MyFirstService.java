package com.example.zhoukai.servicetest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyFirstService extends Service {

    public final String TAG = "MyFirstService";

    public MyFirstService() {
    }

    public void onCreat(){
        super.onCreate();
        Log.d(TAG, "----onCreat----");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "----onBind----");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public int onStartCommand(Intent intent, int flags, int started){
        Log.d(TAG, "----onStartCommand----");
        return START_STICKY;
    }

    public void onDestory(){
        super.onDestroy();
        Log.d(TAG, "----onDestory----");
    }
}
