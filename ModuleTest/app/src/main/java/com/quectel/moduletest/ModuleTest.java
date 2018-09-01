package com.quectel.moduletest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ModuleTest extends AppCompatActivity {
    public static final String TAG = "ModuleTest";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_test);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, DatabaseTestkFragment.newInstance("str1", "str2"))
                .commit();
    }
}
