package com.example.klein.mydemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity{
	public final String TAG = "sensor-klein";
    private SensorManager mSensorManager;
    private MySensorEventListener mSensorEventListener;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        startSensor();
    }
 
    /**
     * 启动传感器，监测设备的运动位移。
     */
    private void startSensor() {
        mSensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager == null) {
            throw new UnsupportedOperationException();
        }
 
        Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //Sensor mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        Log.d(TAG, "startSensor");
        if (mSensor == null) {
            throw new UnsupportedOperationException("设备不支持");
        }
 
        //加速度传感器
        mSensorEventListener = new MySensorEventListener();
 
        Log.d(TAG, "registerListener");
        boolean isRegister = mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        if (!isRegister) {
            throw new UnsupportedOperationException("设备不支持");
        }
    }
 
    private class MySensorEventListener implements SensorEventListener {
        private float x_old = 0, y_old = 0, z_old = 0;
        private float x_new = 0, y_new = 0, z_new = 0;
        private float delta_x = 0, delta_y = 0, delta_z = 0;
        private double delta = 0;
 
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            (new Exception("klein---onSensorChanged")).printStackTrace();
            x_new = sensorEvent.values[0];    //x坐标轴
            y_new = sensorEvent.values[1];    //y坐标轴
            z_new = sensorEvent.values[2];    //z坐标轴
 
            delta_x = x_new - x_old;
            delta_y = y_new - y_old;
            delta_z = z_new - z_old;
 
            //此处建立不严格的数学模型，计算运动量，从而得出位移的值。
            delta = Math.sqrt(delta_x * delta_x + delta_y * delta_y + delta_z * delta_z);
            Log.d(TAG, "x = " + x_new + ", y = " + y_new + ", z_new = " + z_new + ", delta" + delta);
 
            x_old = x_new;
            y_old = y_new;
            z_old = z_new;
        }
 
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
 
        }
    }
 
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(mSensorEventListener);
    }

}
