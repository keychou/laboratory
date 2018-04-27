package com.example.zhoukai.servicetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ServiceTest extends AppCompatActivity {

    Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_test);

        bt = (Button) findViewById(R.id.button);


//        Intent intent = new Intent();
//        intent.setClassName("com.example.zhoukai.servicetest","com.example.zhoukai.servicetest.MyFirstService");
//      //  intent.setAction("com.example.zhoukai.servicetest.MyFirstService");
//        startService(intent);

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClassName("com.qualcomm.qti.modemtestmode","com.qualcomm.qti.modemtestmode.MbnFileLoadService");
                startService(intent);
            }
        });





    }
}
