package com.quectel.moduletest;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ModuleTest extends AppCompatActivity implements StorageTestFragment.OnFragmentInteractionListener {
    public static final String TAG = "ModuleTest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_test);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, StorageTestFragment.newInstance("str1", "str2"))
                .commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
