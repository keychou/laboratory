package com.quectel.camerademo;

import android.hardware.Camera;

import java.util.concurrent.ArrayBlockingQueue;
import com.quectel.camerademo.encoder.MediaMuxerRunnable;

/**
 * Created by zhoukai on 17-5-12.
 */

public class PreviewStreamRecorder implements Camera.PreviewCallback
{
    int YUVIMGLEN;
    int width = 1280;
    int height = 720;
    int framerate = 30;
    int biterate = 16*1024*1024;

    private static int yuvqueuesize = 10;
    public static ArrayBlockingQueue<byte[]> YUVQueue = new ArrayBlockingQueue<byte[]>(yuvqueuesize);

    MediaMuxerRunnable mMediaMuxerRunnable;

    private int mCameraId;

    PreviewStreamRecorder(int cameraId){
        //avcCodec = new AvcEncoder(width,height,framerate,biterate);
        //avcCodec.StartEncoderThread();
        mCameraId = cameraId;
        mMediaMuxerRunnable = new MediaMuxerRunnable(mCameraId);
        startRecording();
    }

    public void close() {
        //stopRecording();
        mMediaMuxerRunnable.stopMediaMuxer();
    }

    private void startRecording() {
        mMediaMuxerRunnable.startMuxer();
    }

    private void stopRecording() {
        mMediaMuxerRunnable.stopMuxer();
    }


    public void onPreviewFrame(byte[] data, Camera camera)
    {
        mMediaMuxerRunnable.addVideoFrameData(data);
        camera.addCallbackBuffer(data);
    }
}
