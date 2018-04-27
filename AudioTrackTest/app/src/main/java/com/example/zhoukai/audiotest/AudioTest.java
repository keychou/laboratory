package com.example.zhoukai.audiotest;

import android.content.DialogInterface;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AudioTest extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG="AudioTest";

    Button play, stoppl, record, stopre;
    private InputStream mFile;

    WavRecorder wavRecorder = new WavRecorder(8000, 2, (Environment.getExternalStorageDirectory().getAbsolutePath() + "/re.wav"));
    WavPlayer wavPlayer = new WavPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_test);

        play = (Button) findViewById(R.id.play);
        stoppl = (Button) findViewById(R.id.stoppl);
        record = (Button) findViewById(R.id.record);
        stopre = (Button) findViewById(R.id.stopre);

        play.setOnClickListener(this);
        stoppl.setOnClickListener(this);
        record.setOnClickListener(this);
        stopre.setOnClickListener(this);

        try {
            //mFile = getAssets().open("raw/" + "stop_the_time_28s.wav");
            //mFile = new FileInputStream("/sdcard/Music/stop_the_time_28s.wav");
            mFile = new FileInputStream("/sdcard/re.wav");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.play:
                Log.d(TAG, "--wav play-----");
                wavPlayer.playStream(mFile);
                break;
            case R.id.stoppl:
                Log.d(TAG, "--stop play-----");
                break;
            case R.id.record:
                Log.d(TAG, "--wav record-----");
                wavRecorder.startRecord();
                break;
            case R.id.stopre:
                Log.d(TAG, "--stop record-----");
                wavRecorder.stopRecord();
                break;
            default:

                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
