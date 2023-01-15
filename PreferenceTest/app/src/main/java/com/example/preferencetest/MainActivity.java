package com.example.preferencetest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private Button Settings, Show;
    private TextView tvCheckout,tvList,tvEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Settings = (Button)findViewById(R.id.setings);
        Show = (Button)findViewById(R.id.show);

        tvCheckout = (TextView)findViewById(R.id.tv_checkout);
        tvList = (TextView)findViewById(R.id.tv_list);
        tvEditText = (TextView)findViewById(R.id.tv_edittext);

        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,SettingsPreference.class));
            }
        });

        Show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingInfo();
            }
        });
    }


    private void showSettingInfo() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        tvCheckout.setText(settings.getBoolean(SettingsPreference.CHECKBOX_KEY, false)+"");
        tvEditText.setText(settings.getString (SettingsPreference.EDIT_KEY, ""));
        tvList.setText(settings.getString(SettingsPreference.LIST_KEY, "linc"));
    }
}