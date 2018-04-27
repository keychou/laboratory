package com.example.skylar.aidlservice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by skylar on 2016/5/8.
 */
public class AidlService extends Service{
    private CatBinder catBinder;
    Timer timer = new Timer();
    String[] colors = new String[]{
            "red",
            "yellow",
            "black"
    };

    double[] weights = new double[]{
            2.3,
            3.1,
            1.5
    };

    private String color;
    private double weight;


    public class CatBinder extends ICat.Stub{
        public String getColor() throws android.os.RemoteException{
            return color;
        }
        public double getWeight() throws android.os.RemoteException{
            return weight;
        }

        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, java.lang.String aString) throws android.os.RemoteException{

        }
    }

    public void onCreate(){
        super.onCreate();
        System.out.println("skylar１---AidlService-----------onCreate");
        catBinder = new CatBinder();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int rand = (int)(Math.random()*3);
                color = colors[rand];
                weight = weights[rand];
            }
        }, 0, 800);
    }

    public IBinder onBind(Intent arg0){
        System.out.println("skylar１---AidlService-----------catBinder = " + catBinder);
        (new Exception()).printStackTrace();
        return catBinder;
    }
}
