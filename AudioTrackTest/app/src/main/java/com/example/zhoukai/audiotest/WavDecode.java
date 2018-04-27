package com.example.zhoukai.audiotest;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by zhoukai on 16-12-5.
 */

public class WavDecode {


    private final static String TAG="WavDecode";

    public class WavFormat{

        short format;
        short channels;
        int rate;
        short bits;
        int dataSize;

        WavFormat(short format,short channels, int rate, short bits, int dataSize){
            this.format = format;
            this.channels = channels;
            this.rate = rate;
            this.bits = bits;
            this.dataSize = dataSize;
        }
    }

    public WavFormat readHeader(InputStream wavStream) throws IOException {

        Log.d(TAG, "--readHeader-----");
        short format, channels, bits;
        int rate, dataSize;

        ByteBuffer buffer = ByteBuffer.allocate(44);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        wavStream.read(buffer.array(), buffer.arrayOffset(), buffer.capacity());
        buffer.rewind();
        buffer.position(buffer.position() + 20);
        format = buffer.getShort();  // --格式
        channels = buffer.getShort();  // --声道
        rate = buffer.getInt(); // --采样率
        buffer.position(buffer.position() + 6);
        bits = buffer.getShort(); // --采样精度
        Log.d("TAG", "readHeader: format=" + format + "--channels=" + channels + "--rate=" + rate + "--bits=" + bits);
      //  Log.d(TAG, "--buffer.getInt() = " + buffer.getInt());
        while (buffer.getInt() != 0x61746164) { // "data" marker
            int size = buffer.getInt();
            wavStream.skip(size);
            Log.d(TAG, "--buffer---skip");
            buffer.rewind();
            wavStream.read(buffer.array(), buffer.arrayOffset(), 8);
            Log.d(TAG, "--buffer---read2");
            buffer.rewind();
        }
        dataSize = buffer.getInt();  // -- 数据长度
        Log.d("TAG", "readHeader: format=" + format + "--channels=" + channels + "--rate=" + rate + "--bits=" + bits + "--dataSize=" + dataSize);
        WavFormat wavFormat = new WavFormat(format,channels, rate,  bits, dataSize);
        return wavFormat;
    }
}
