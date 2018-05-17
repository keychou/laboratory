package com.quectel.camerademo;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by klein on 17-6-27.
 */

public class MainStreamRecorder {

    public final String TAG = "CameraDemo.MSR";

    private Context mContext;
    private int mCameraId;
    private Camera mCamera;
    FileUtils fileUtils = new FileUtils();

    private MediaRecorder mMediaRecorder;

    MainStreamRecorder(Context context, int cameraId, Camera camera){
        Log.d(TAG, "init MainStreamRecorder");
        mContext = context;
        mCameraId = cameraId;
        mCamera = camera;

        Log.d(TAG, "cameraId = " + cameraId +  ", camera = " + camera);
    }


    public boolean startFrontRecording() {
        if (mMediaRecorder != null) {
            Log.d(TAG, "media recorder start");
            mMediaRecorder.start();
            return true;
        } else {
            Log.d(TAG, "media recorder release");
            releaseFrontMediaRecorder();
        }
        return false;
    }

    public void stopFrontRecording() {
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
        }
        releaseFrontMediaRecorder();
    }


    public MediaRecorder creatVideoRecorder() {

        mCamera.unlock();
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT){
            mMediaRecorder.setVideoSize(1280, 720);
            mMediaRecorder.setVideoEncodingBitRate(16 * 1024 * 1024);
            mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.setOutputFile(fileUtils.getOutputMediaFile(FileUtils.MediaType.MEDIA_TYPE_VIDEO_FRONT).toString());
        }else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            mMediaRecorder.setVideoSize(1280, 720);
            mMediaRecorder.setVideoEncodingBitRate(16 * 1024 * 1024);
            mMediaRecorder.setVideoFrameRate(30);
            mMediaRecorder.setOutputFile(fileUtils.getOutputMediaFile(FileUtils.MediaType.MEDIA_TYPE_VIDEO_BACK).toString());
        }


        return mMediaRecorder;
    }

    public boolean prepareVideoRecorder(MediaRecorder mediaRecorder) {
        try {
            mMediaRecorder = mediaRecorder;
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing fMediaRecorder: " + e.getMessage());
            releaseFrontMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing fMediaRecorder: " + e.getMessage());
            releaseFrontMediaRecorder();
            return false;
        }

        return true;
    }


        private void releaseFrontMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mCamera.lock();
        }
    }
}
