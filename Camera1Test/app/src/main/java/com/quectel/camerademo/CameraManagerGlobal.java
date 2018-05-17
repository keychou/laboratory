package com.quectel.camerademo;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by klein on 17-6-27.
 */

public class CameraManagerGlobal {

    public final String TAG = "CameraDemo" + "." + this.getClass().getName();


    private Context mContext;
    private int mCameraId;
    private Camera mCamera;
    FileUtils fileUtils = new FileUtils();
    CameraSettings frontCameraSetting, BackCameraSetting;


    CameraManagerGlobal(Context context){
        mContext = context;
    }

    void initCameraSetting(){
        frontCameraSetting = new CameraSettings(mCamera);
        BackCameraSetting = new CameraSettings(mCamera);
    }

    /*find front camera*/
    public int findFirstfrontFacingCamera() {
        int numCams = Camera.getNumberOfCameras();
        for (int camId = 0; camId < numCams; camId++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(camId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                mCameraId = camId;
                break;
            }
        }
        return mCameraId;
    }


    /*find back camera*/
    public int findFirstBackFacingCamera() {
        int numCams = Camera.getNumberOfCameras();
        for (int camId = 0; camId < numCams; camId++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(camId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCameraId = camId;
                break;
            }
        }
        return mCameraId;
    }

    public  int[] getMaxPreviewFpsRange(Camera.Parameters params) {
        List<int[]> frameRates = params.getSupportedPreviewFpsRange();
        if (frameRates != null && frameRates.size() > 0) {
            return frameRates.get(frameRates.size() - 1);
        }
        return new int[0];
    }


}
