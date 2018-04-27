package com.zhoukaihffoxmail.switchertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_LONG;

public class SwitcherTest extends AppCompatActivity {

    private Switch aSwitch;
    private int lteState = 0;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switcher_test);
        aSwitch = (Switch) findViewById(R.id.switch1);
        textView = (TextView) findViewById(R.id.textView);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    textView.setText(R.string.note_lte_on);
                } else {
                    textView.setText(R.string.note_lte_off);
                }
            }
        });
    }

    public int openLte(){
        if (lteState == 1){
            return 0;
        }

        return 0;
    }

    public int closeLte(){
        if (lteState == 1 ){
            return 0;
        }

        return 0;
    }
}
