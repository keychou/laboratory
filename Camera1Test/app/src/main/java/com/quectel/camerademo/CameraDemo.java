package com.quectel.camerademo;

import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CameraDemo extends AppCompatActivity implements DefaultFragment.OnFragmentInteractionListener {


    public static final String TAG = "CameraDemo";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_demo);


        getFragmentManager().beginTransaction()
                .replace(R.id.container, TsFromatRecordTestFragment.newInstance("str1", "str2"))
                .commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri){

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
