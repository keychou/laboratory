package com.quectel.camera2test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Camera2Test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2_test);

        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2PreviewTestFragment.newInstance())
                    .commit();
        }
    }
}
