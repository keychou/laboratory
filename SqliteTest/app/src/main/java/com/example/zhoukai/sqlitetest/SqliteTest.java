package com.example.zhoukai.sqlitetest;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.OPEN_READONLY;

public class SqliteTest extends AppCompatActivity {

    public final String TAG = "SqliteTest";

    MbnPolicyManager mbnPolicyManager;
    TelephonyManager mTm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sqlite_test);


        Intent intent = new Intent();
        intent.setClassName("com.qualcomm.qti.modemtestmode","com.qualcomm.qti.modemtestmode.MbnFileLoadService");
        //  intent.setAction("com.example.zhoukai.servicetest.MyFirstService");
        Log.d(TAG, "start service");
        startService(intent);



      //  TelephonyManager mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

     //   mbnPolicyManager = new MbnPolicyManager(getApplicationContext());

      //  mbnPolicyManager.findMbnPath();
        finish();
    }


}
