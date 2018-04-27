package com.example.zhoukai.broadcasttest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class BroadcastTest extends AppCompatActivity {

    public static final String TAG = "BroadcastTest";
    public static final String EXTERNAL_REQUEST_DIAL =
            "com.android.dialer.EXTERNAL_REQUEST_DIAL";



    public static final String EXTERNAL_REQUEST_HANG_UP_CALL =
            "com.android.dialer.EXTERNAL_REQUEST_HANG_UP_CALL";

    Button call, end_call;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_test);

        call = (Button) findViewById(R.id.call);
        end_call = (Button) findViewById(R.id.end_call);


        end_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Log.d(TAG, "end_call");
                Intent intent = new Intent();
                intent.setAction(EXTERNAL_REQUEST_HANG_UP_CALL);
                sendBroadcast(intent);
            }
        });


        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "start call");
                Intent intent = new Intent();
                intent.setAction(EXTERNAL_REQUEST_DIAL);
                sendBroadcast(intent);

               // TODO Auto-generated method stub
              //  String number = "18326602200";
                //用intent启动拨打电话
             //   Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+number));
             //   startActivity(intent);
            }
        });
    }
}
