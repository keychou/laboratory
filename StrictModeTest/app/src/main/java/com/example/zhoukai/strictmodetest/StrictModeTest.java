package com.example.zhoukai.strictmodetest;

import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StrictModeTest extends AppCompatActivity {

    Button start_thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_strict_mode_test);

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build()
        );


        start_thread = (Button) findViewById(R.id.start_thread);


        //java.lang.Throwable: Explicit termination method 'close' not called NOT appear everytime , so we need to click the button servral times until
        //this reproduced.
        start_thread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyThreadTest().start();
            }
        });

    }


    public class MyThreadTest extends Thread {

        MyThreadTest()
        {
            setName("Thread Test");
        }

        @Override
        public void run()
        {
            Log.d("StrictModeTest","Android strict mode test");
            File newxmlfile = new File(Environment.getExternalStorageDirectory(), "castiel.txt");
            try {
                newxmlfile.createNewFile();
                FileWriter fw = new FileWriter(newxmlfile);
                fw.write("Android strict mode test");
                //fw.close(); 我们在这里特意没有关闭 fw

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}




