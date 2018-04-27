package com.zhoukaihffoxmail.gesturedetectortest;

import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class GestureDetectorTest extends AppCompatActivity{

    private static final String TAG = "GestureDetectorTest";

    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_detector_test);
        mDetector = new GestureDetectorCompat(this, new MyGestureListener());
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        mDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String TAG = "Gestures";

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(TAG,"onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {
            Log.d(TAG, "onFling: " + event1.toString()+event2.toString());
            return true;
        }
    }
}
