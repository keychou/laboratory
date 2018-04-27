package com.quectel.asynctasktest;

/**
 * Created by klein on 17-8-14.
 */

public class NetOperator {
    public void operator(){
        try {
            //休眠1秒
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
