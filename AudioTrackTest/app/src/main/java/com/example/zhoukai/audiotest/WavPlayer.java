package com.example.zhoukai.audiotest;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zhoukai on 16-12-7.
 */

public class WavPlayer {

    private final static String TAG="WavPlayer";

    AudioTrack mTrack=null;
    private byte mBuffer[];
    private short mLen;
    int bufsize;
    InputStream mInputStream;

    WavPlayer(InputStream inputStream){
        mInputStream = inputStream;

    }

    WavPlayer(){

    }

    void playStatic(InputStream inputStream) {

        if (mTrack == null && inputStream != null) {
            try {
                WavDecode wavDecode = new WavDecode();
                WavDecode.WavFormat wavformat = wavDecode.readHeader(inputStream);


                mBuffer = new byte[1024 * 1024];
                mLen = (short) inputStream.read(mBuffer);
                // AudioTrack.MODE_STREAM(不能重复播放) ;AudioTrack.MODE_STATIC(可以重复播放)
                mTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        wavformat.rate,  //每秒8K个点(采样率)
                        AudioFormat.CHANNEL_OUT_DEFAULT, //双声道
                        AudioFormat.ENCODING_PCM_16BIT, //一个采样点16比特-2个字节(设置音频数据块是8位还是16位-采样精度)
                        mLen,
                        AudioTrack.MODE_STATIC);
            } catch (Exception e) {
            }
        }
        if (mTrack != null) {
            short written = (short) mTrack.write(mBuffer, 0, mLen);
            mTrack.play();
        }
    }


    void playStream(InputStream inputStream) {
        PlayBackStreamThead playBackThead = new WavPlayer.PlayBackStreamThead(inputStream);
        Thread t1 = new Thread(playBackThead);
        t1.start();
    }

    public class PlayBackStreamThead implements Runnable{
        InputStream inputStream;
        PlayBackStreamThead(InputStream inputStream){
            this.inputStream = inputStream;
        }
        @Override
        public void run() {
            Log.d(TAG, "mTrack = " + mTrack + ", inputStream = " + inputStream);
            if (mTrack == null && inputStream != null) {
                Log.d(TAG, "--run play-----");
                try {
                    WavDecode wavDecode = new WavDecode();
                    WavDecode.WavFormat wavformat = wavDecode.readHeader(inputStream);

                    bufsize = AudioTrack.getMinBufferSize(wavformat.rate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
                    mTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                            wavformat.rate,  //每秒8K个点(采样率)
                            AudioFormat.CHANNEL_OUT_STEREO, //双声道
                            AudioFormat.ENCODING_PCM_16BIT, //一个采样点16比特-2个字节(设置音频数据块是8位还是16位-采样精度)
                            bufsize,
                            AudioTrack.MODE_STREAM);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (mTrack != null) {

                mTrack.play();

                int readResult = 0;
                byte buffer[] = new byte[bufsize];
                while (true) {
                    try {
                        readResult = inputStream.read(buffer, 0, bufsize);
                    } catch (IOException e1) { 
                    }
                    if (readResult != -1) {
                        // AudioTrack.PLAYSTATE_PAUSED（暂停状态），
                        mTrack.write(buffer, 0, readResult);
                    } else {
                        break;
                    }
                }

                // -- 输出流可以重复使用
                try {
                    inputStream.close();
                    inputStream.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // -- 重置mTrack状态
                mTrack.stop();
                // -- 释放底层资源。
                mTrack.release();
            }
        }
    }
}
