package com.quectel.camerademo.encoder;

import android.hardware.Camera;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.support.annotation.IntDef;
import android.util.Log;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Vector;
import com.quectel.camerademo.*;


/**
 * Created by robi on 2016-04-01 10:45.
 */
public class MediaMuxerRunnable extends Thread {

    public final String TAG = "CameraDemo.MMR";

    public static final int TRACK_VIDEO = 0;
    public static final int TRACK_AUDIO = 1;
    public static boolean DEBUG = false;
    private static MediaMuxerRunnable mediaMuxerThread;
    private final Object lock = new Object();
    private MediaMuxer mediaMuxer;
    private Vector<MuxerData> muxerDatas;
    private volatile boolean isExit = false;
    private int videoTrackIndex = -1;
    private int audioTrackIndex = -1;
    private volatile boolean isVideoAdd;
    private volatile boolean isAudioAdd;
    private AudioRunnable audioThread;
    private VideoRunnable videoThread;
    private boolean isMediaMuxerStart = false;
    private MediaFormat videoMediaFormat;
    private MediaFormat audioMediaFormat;
    public int mCameraId;
    private boolean hasAudio = false;

    private volatile long writeCount = 0;

    FileUtils fileUtils = new FileUtils();

    public MuxerData mMuxerData;

    public MediaMuxerRunnable(int cameraId) {
        mCameraId = cameraId;
        mediaMuxerThread = this;
    }

    public void startMuxer() {

        mediaMuxerThread.start();

    }

    public void stopMuxer() {
        if (mediaMuxerThread != null) {
            mediaMuxerThread.exit();
            try {
                mediaMuxerThread.join();
            } catch (InterruptedException e) {

            }
            mediaMuxerThread = null;
        }
    }

    public void addVideoFrameData(byte[] data) {
        if (mediaMuxerThread != null) {
            mediaMuxerThread.addVideoData(data);
        }
    }

    private void initMuxer() {
        muxerDatas = new Vector<>();
        audioThread = new AudioRunnable(this);
        videoThread = new VideoRunnable(CameraSettings.IMAGE_WIDTH, CameraSettings.IMAGE_HEIGHT, this);

        audioThread.start();
        videoThread.start();

        restartMediaMuxer();
    }

    private void addVideoData(byte[] data) {
        if (videoThread != null) {
            videoThread.add(data);
        }
    }

    private void restartMediaMuxer() {
        try {
            resetMediaMuxer();
            Log.d(TAG, "restart MediaMuxer-" + mCameraId + ", videoMediaFormat = " + videoMediaFormat + ", audioMediaFormat = " + audioMediaFormat);
            if (videoMediaFormat != null) {
                videoTrackIndex = mediaMuxer.addTrack(videoMediaFormat);
                Log.d(TAG, "restart  MediaMuxer-" + mCameraId + ", set isVideoAdd true");
                isVideoAdd = true;
            }
            if (audioMediaFormat != null) {
                audioTrackIndex = mediaMuxer.addTrack(audioMediaFormat);
                Log.d(TAG, "restart MediaMuxer-" + mCameraId + ", set isAudioAdd true");
                isAudioAdd = true;
            }
            requestStart();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopMediaMuxer() {
        if (mediaMuxer != null) {
            try {
                mediaMuxer.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mediaMuxer.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isAudioAdd = false;
            isVideoAdd = false;
            isMediaMuxerStart = false;
            mediaMuxer = null;

            restartAudioVideo();
        }
    }

    private void resetMediaMuxer() throws Exception {
        stopMediaMuxer();
        String path;
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT){
            path = (fileUtils.getOutputMediaFile(FileUtils.MediaType.MEDIA_TYPE_VIDEO_FRONT_PREVIEW_MP4)).toString();
        } else {
            path = (fileUtils.getOutputMediaFile(FileUtils.MediaType.MEDIA_TYPE_VIDEO_BACK_PREVIEW_MP4)).toString();
        }

        mediaMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        Log.d(TAG, "create MediaMuxer-" + mCameraId + " , save to :" + path);
    }

    public synchronized void setMediaFormat(@TrackIndex int index, MediaFormat mediaFormat) {
        if (mediaMuxer == null) {
            return;
        }

        Log.d(TAG, "setMediaFormat MediaMuxer-" + mCameraId + ", videoMediaFormat = " + videoMediaFormat + ", audioMediaFormat = " + audioMediaFormat);

        if (index == TRACK_VIDEO) {
            if (videoMediaFormat == null) {
                videoMediaFormat = mediaFormat;
                videoTrackIndex = mediaMuxer.addTrack(mediaFormat);
                Log.d(TAG, "setMediaFormat MediaMuxer-" + mCameraId + ", set isVideoAdd true");
                isVideoAdd = true;
            }
        } else {
            if (audioMediaFormat == null) {
                audioMediaFormat = mediaFormat;
                audioTrackIndex = mediaMuxer.addTrack(mediaFormat);
                Log.d(TAG, "setMediaFormat MediaMuxer-" + mCameraId + ", set isAudioAdd true");
                isAudioAdd = true;
            }
        }

        requestStart();
    }

    private void exit() {
        if (videoThread != null) {
            videoThread.exit();
            try {
                videoThread.join();
            } catch (InterruptedException e) {

            }
        }
        if (audioThread != null) {
            audioThread.exit();
            try {
                audioThread.join();
            } catch (InterruptedException e) {

            }
        }

        isExit = true;
        synchronized (lock) {
            lock.notify();
        }
    }


    public void addMuxerData(int trackIndex, ByteBuffer byteBuf, MediaCodec.BufferInfo bufferInfo) {

        MuxerData data = new MuxerData(trackIndex,byteBuf,bufferInfo);
        if (muxerDatas == null) {
            return;
        }
        muxerDatas.add(data);
        synchronized (lock) {
            lock.notify();
        }
    }


    @Override
    public void run() {
        initMuxer();
        while (!isExit) {
            if (isMediaMuxerStart) {
                if (DEBUG)
                     Log.d(TAG, "MediaMuxer-" + mCameraId + " start !");
                //混合器开启后
                if (muxerDatas.isEmpty()) {
                    synchronized (lock) {
                        try {
                            if (DEBUG)
                                Log.d(TAG, "MediaMuxer-" + mCameraId + ", no data come in, wait data ...");
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
//                    if (fileSwapHelper.requestSwapFile()) {
//                        //需要切换文件
//                        String nextFileName = fileSwapHelper.getNextFileName();
//                        if (DEBUG) Log.d("angcyo-->", "正在重启混合器..." + nextFileName);
//                        restartMediaMuxer();
//                    } else {
                        MuxerData data = muxerDatas.remove(0);
                        int track;
                        if (data.trackIndex == TRACK_VIDEO) {
                            if (DEBUG)
                                Log.d(TAG, "MediaMuxer-" + mCameraId + ", video track in, muxer it");
                            track = videoTrackIndex;
                        } else {
                            if (DEBUG)
                                Log.d(TAG, "MediaMuxer-" + mCameraId + ", audio track in, muxer it");
                            track = audioTrackIndex;
                        }
                        if (DEBUG)
                            Log.d(TAG, "写入混合数据 " + data.bufferInfo.size);

                        try {
                            mediaMuxer.writeSampleData(track, data.byteBuf, data.bufferInfo);
                        } catch (Exception e) {
//                            e.printStackTrace();
//                            if (DEBUG)
                            if (DEBUG) Log.d("angcyo-->", "写入混合数据失败!" + e.toString());
                            restartMediaMuxer();
                        }
                    }
//                }
            } else {
                //混合器未开启
                synchronized (lock) {
                    try {
                        //if (DEBUG)
                            Log.d(TAG, "MediaMuxer-" + mCameraId + " is not start, wait ...");
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        stopMediaMuxer();
//        readyStop();
        if (DEBUG) Log.e(TAG, "MediaMuxer exit !");
    }

    private void requestStart() {
        synchronized (lock) {
            if (isMuxerStart()) {
                mediaMuxer.start();
                isMediaMuxerStart = true;
                //if (DEBUG)
                    Log.d(TAG, "MediaMuxer start ! wait data ...");
                lock.notify();
            }
        }
    }

    private boolean isMuxerStart() {
        return isAudioAdd && isVideoAdd;
    }

    private void restartAudioVideo() {
        if (audioThread != null) {
            audioTrackIndex = -1;
            isAudioAdd = false;
            audioThread.restart();
        }
        if (videoThread != null) {
            videoTrackIndex = -1;
            isVideoAdd = false;
            videoThread.restart();
        }
    }

    @IntDef({TRACK_VIDEO, TRACK_AUDIO})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TrackIndex {
    }

    /**
     * 封装需要传输的数据类型
     */
    public class MuxerData {
        int trackIndex;
        ByteBuffer byteBuf;
        MediaCodec.BufferInfo bufferInfo;

        public MuxerData(@TrackIndex int trackIndex, ByteBuffer byteBuf, MediaCodec.BufferInfo bufferInfo) {
            this.trackIndex = trackIndex;
            this.byteBuf = byteBuf;
            this.bufferInfo = bufferInfo;
        }
    }

}
