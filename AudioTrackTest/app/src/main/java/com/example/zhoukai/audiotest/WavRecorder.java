package com.example.zhoukai.audiotest;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * Created by zhoukai on 16-12-7.
 */

public class WavRecorder {

    static final String TAG = WavRecorder.class.getSimpleName();

    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int AUDIO_FORMAT_IN_BYTE = 2;

    WavEncode 	mWavWriter;
    AudioRecord	mAudioRecord;
    boolean		mStopFlag = false;
    int			mBufSize;
    int			mCurAmplitude = 0;

    RecordThread	mRecordThread;

    public WavRecorder(int sampleRate, int channelCnt, String filePath) {
        int channelConfig = channelCnt == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO;

        Log.d(TAG, "channelConfig: " + channelConfig);

        int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, AUDIO_FORMAT);
        mBufSize = sampleRate * 20 / 1000 * channelCnt * AUDIO_FORMAT_IN_BYTE;
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, AUDIO_FORMAT, 8 * minBufSize);
        mWavWriter = new WavEncode(filePath, channelCnt, sampleRate, AUDIO_FORMAT);
        Log.d(TAG, "state: " + mAudioRecord.getState());
    }

    WavRecorder(){

    }


    public void startRecord() {
        Log.d(TAG, "startRecord");
        mRecordThread = new RecordThread();
        mRecordThread.start();
    }

    public void stopRecord() {
        Log.d(TAG, "stopRecord");
        mStopFlag = true;
        try {
            mRecordThread.join();
        } catch (InterruptedException e) {
            Log.e(TAG, "InterruptedException " + e.getMessage());
        }
    }

    public int getAmplitude() {
        return mCurAmplitude;
    }

    class RecordThread extends Thread {
        @Override
        public void run() {
            Log.d(TAG, "thread run");
      //    Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

            if (mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED) {
                Log.e(TAG, "unInit");
                return;
            }

            byte[] buffer = new byte[mBufSize];
            mAudioRecord.startRecording();
            while (!mStopFlag) {
                int len = mAudioRecord.read(buffer, 0, buffer.length);
                mWavWriter.writeToFile(buffer, len);
                setCurAmplitude(buffer, len);
            }
            mWavWriter.closeFile();
            mAudioRecord.stop();
            mAudioRecord.release();
            Log.d(TAG, "thread end");
        }
    }

    private void setCurAmplitude(byte[] readBuf, int read) {
        mCurAmplitude = 0;
        for (int i = 0; i < read / 2; i++) {
            short curSample = (short) ((readBuf[i * 2] & 0xFF) | (readBuf[i * 2 + 1] << 8));
            if (curSample > mCurAmplitude) {
                mCurAmplitude = curSample;
            }
        }
    }
}
