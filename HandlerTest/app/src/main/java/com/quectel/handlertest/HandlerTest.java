package com.quectel.handlertest;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class HandlerTest extends AppCompatActivity {
    public static final String TAG = "HandlerTest";

    MainHandler mainHandler;
    Handler subHandler;

    private HandlerThread myHandlerThread ;
    Handler subHandler2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handler_test);

        Log.d( TAG , "main thread id = " + Thread.currentThread().getId()) ;

        mainHandler = new MainHandler();

        //TEST 1: 子线程通过主线程handler给子线程发送消息
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                msg.what = 0x01;
                msg.arg1 = 1;
                mainHandler.sendMessage(msg);
            }
        }).start();


        //TEST 2:  =======主线程获取子线程handler给子线程发送消息=======
        SubThread subThread = new SubThread();
        new Thread(subThread).start();

        try{
            Thread.sleep(2000);
        }catch (Exception e){
            e.printStackTrace();
        }


        subHandler = subThread.getHandler();

        Message msg = Message.obtain();
        msg.what = 0x05;
        msg.arg1 = 2;
        subHandler.sendMessage(msg);


        //TEST 3. =======使用HandlerThread创建子线程=======

        myHandlerThread = new HandlerThread( "handler-thread") ;
        myHandlerThread.start();
        subHandler2 = new Handler(myHandlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //这个方法是运行在 handler-thread 线程中的 ，可以执行耗时操作
                Log.d( TAG , "subHandler2 thread id = " + Thread.currentThread().getId()) ;

            }
        };

        Message msg1 = Message.obtain();
        msg1.what = 0x06;
        msg1.arg1 = 3;
        subHandler2.sendMessage(msg1);

    }

    public class MainHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Log.d(TAG, "main handler msg = " + msg);
        }
    }



    public class SubThread implements Runnable{
        SubHandler subHandler;
        @Override
        public void run() {
            Message msg = Message.obtain();
            msg.what = 0x02;
            msg.arg1 = 1;
            mainHandler.sendMessage(msg);
            Log.d(TAG, "-----begin loop------");
            Looper.prepare();
            subHandler = new SubHandler();
            Looper.loop();

            Log.d(TAG, "-----end loop------");
        }

        public class SubHandler extends Handler{
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, "sub handler msg = " + msg);
                Log.d( TAG , "SubHandler thread id = " + Thread.currentThread().getId()) ;
            }
        }

        Handler getHandler(){
            return subHandler;
        }
    }

}
