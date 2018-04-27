package com.quectel.cameratuning;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.ConditionVariable;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static junit.framework.Assert.fail;

public class CameraTuning extends AppCompatActivity {

    private static final String TAG = AppCompatActivity.class.getSimpleName();

    private Map<String, Gpio> mGpioMap = new LinkedHashMap<>();

    Camera mCamera;
    SurfaceTexture mSurfaceTexture;
    GLSurfaceView.Renderer mRenderer;

    List<Camera.Size> previewSizes;
    List<int[]> fpsRanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_tuning);


        LinearLayout gpioPinsView = (LinearLayout) findViewById(R.id.gpio_pins);
        LayoutInflater inflater = getLayoutInflater();

        mCamera = Camera.open(0);
        Camera.Parameters parameters = mCamera.getParameters();
        previewSizes = parameters.getSupportedPreviewSizes();
        fpsRanges = parameters.getSupportedPreviewFpsRange();

        PeripheralManager pioManager = PeripheralManager.getInstance();

        for (String name : pioManager.getGpioList()) {
            View child = inflater.inflate(R.layout.list_item_gpio, gpioPinsView, false);
            Switch button = (Switch) child.findViewById(R.id.gpio_switch);
            button.setText(name);
            gpioPinsView.addView(button);
            Log.d(TAG, "Added button for GPIO: " + name);

            try {
                final Gpio ledPin = pioManager.openGpio(name);
                ledPin.setEdgeTriggerType(Gpio.EDGE_NONE);
                ledPin.setActiveType(Gpio.ACTIVE_HIGH);
                ledPin.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);

                button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        try {
                            ledPin.setValue(isChecked);
                        } catch (IOException e) {
                            Log.e(TAG, "error toggling gpio:", e);
                            buttonView.setOnCheckedChangeListener(null);
                            // reset button to previous state.
                            buttonView.setChecked(!isChecked);
                            buttonView.setOnCheckedChangeListener(this);
                        }
                    }
                });

                mGpioMap.put(name, ledPin);
            } catch (IOException e) {
                Log.e(TAG, "Error initializing GPIO: " + name, e);
                // disable button
                button.setEnabled(false);
            }
        }
    }


    private void initializeMessageLooper(final int cameraId) {
        final ConditionVariable startDone = new ConditionVariable();
        new Thread() {
            @Override
            public void run() {
                Log.v(TAG, "Start camera/surfacetexture thread");
                mCamera = Camera.open(cameraId);
            }
        }.start();

    }

}
