package com.quectel.parceltest;

import android.content.Intent;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ParcelTest extends AppCompatActivity {
    public static final String TAG = "ParcelTest";
    private MyParcel myparcel1;
    private MyParcel myparcel2;
    byte[] bytedata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parcel_test);

        //简单测试Parcel如何赋值, Parcel write -> Parcel read
        Parcel p1 = Parcel.obtain();
        p1.writeString("It's a test of parcel");
        p1.writeInt(0001);
        p1.writeInt(18);
        p1.writeString("pps");
        p1.setDataPosition(0);

        (new Exception()).printStackTrace();
        myparcel1 = new MyParcel(p1);
        p1.recycle();

        Log.d(TAG , myparcel1.toString());


        //测试Parcel的序列化与反序列化, Parcel write -> byte[] -> Parcel read
        Parcel p2 = Parcel.obtain();
        p2.writeString("It's another test of parcel");
        p2.writeInt(0002);
        p2.writeInt(28);
        p2.writeString("word");

        bytedata = p2.marshall();
        myparcel2 = new MyParcel(bytedata);
        p2.recycle();

        Log.d(TAG , myparcel2.toString());

        //测试使用Parcelable在Activity间传输数据
        Intent intent=new Intent();
        intent.setClass(this, SubActivity.class);
        intent.putExtra("MyParcel", myparcel2);
        startActivity(intent);
    }
}
