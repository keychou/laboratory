package com.zhoukaihffoxmail.downloadtest;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DownloadTest extends Activity {
    public static final String TAG= "DownloadTest";
    private Button btDownload, btOpenGprs;
    public boolean isGprsConnected = false;
    TextView tvShowStatus;
    int mDownloadCount = 0;
    boolean mDownloadStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_test);

        final ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);


        NetworkRequest.Builder builder = new NetworkRequest.Builder();

        builder.addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED);
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);

        NetworkRequest networkRequest = builder.build();
        connectivityManager.setProcessDefaultNetwork(null);

        ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {

            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                Log.i(TAG, "find the proper network");
                Log.i(TAG, "network = " + network);
                // 只要一找到符合条件的网络就注销本callback
                // 你也可以自己进行定义注销的条件
                //connectivityManager.unregisterNetworkCallback(this);
                isGprsConnected = true;
                Log.d(TAG, "Connect to GPRS");
                connectivityManager.setProcessDefaultNetwork(network);

                if (!mDownloadStart){
                    mDownloadStart = true;
                    //scheduleDownload();
                    //mScheduleHandler1.postDelayed(r1, 10*1000);
                }
            }

            public void onLosing(Network network, int maxMsToLive) {
                Log.i(TAG, "onLosing-----network = " + network);
            }

            /**
             * Called when the framework has a hard loss of the network or when the
             * graceful failure ends.
             *
             * @param network The {@link Network} lost.
             */
            public void onLost(Network network) {
                Log.i(TAG, "onLost-----network = " + network);
            }

            /**
             * Called if no network is found in the given timeout time.  If no timeout is given,
             * this will not be called.
             * @hide
             */
            public void onUnavailable() {
                Log.i(TAG, "onUnavailable-----network = ");
            }

        };

        connectivityManager.requestNetwork(networkRequest, networkCallback);
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback);

        btDownload = (Button)this.findViewById(R.id.download);
        btOpenGprs = (Button)this.findViewById(R.id.open_gprs);
        btDownload.setOnClickListener(new DownloadClickListener());
        tvShowStatus = (TextView) findViewById(R.id.show_status);


        btOpenGprs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

                try {

                    Method method = telephonyManager.getClass().getMethod("enableDataConnectivity");
                    method.setAccessible(true);
                    method.invoke(telephonyManager);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private class MyHandler  extends Handler {
        public MyHandler(Looper myLooper){
            super(myLooper);
        }

        public MyHandler(){
        }

        public void handleMessage(Message msg){
            switch (msg.what){
                case Status.GPRS_CONNECTED:
                    tvShowStatus.setText((String)msg.obj);
                    break;
                case Status.GPRS_DISCONNECTED:
                    tvShowStatus.setText((String)msg.obj);
                    break;
                default:
                    break;
            }
        }
    }


    /**
     *
     * @Project: Android_MyDownload
     * @Desciption: 读取任意文件，并将文件保存到手机SDCard
     * @Author: LinYiSong
     * @Date: 2011-3-25~2011-3-25
     */
    class DownloadClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            DownloadThread downloadThread = new DownloadThread(new MyHandler());
            new Thread(downloadThread, "downloadThread").start();
        }

    }

    Handler mScheduleHandler1 = new Handler();
    Runnable r1 = new Runnable() {
        @Override
        public void run() {
            scheduleDownload();
            mScheduleHandler1.postDelayed(r1, 10*1000);
        }
    };


    public void scheduleDownload(){
        Log.d(TAG, "scheduleDownload : " + (++mDownloadCount));
        DownloadThread downloadThread = new DownloadThread(new MyHandler());
        new Thread(downloadThread, "downloadThread").start();
    }

    public class Status{
        public static final int GPRS_CONNECTED = 0x01 << 1;
        public static final int GPRS_DISCONNECTED = 0x01 << 2;
    }

    private class DownloadThread implements Runnable{
        private Handler handler;

        public DownloadThread(Handler handler){
            this.handler = handler;
        }


        @Override
        public void run() {

            ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            Network network = connectivityManager.getProcessDefaultNetwork();
            // if process bind to any network, if not, network will be null, so the network opration will use the default rules.
            if (network == null){
                Message msg = handler.obtainMessage();
                msg.what = Status.GPRS_DISCONNECTED;
                msg.obj = "process don't bind to GPRS, operations prohibited";
                msg.sendToTarget();
                return;
            }

            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo == null){
                Message msg = handler.obtainMessage();
                msg.what = Status.GPRS_DISCONNECTED;
                msg.obj = "process don't bind to GPRS, operations prohibited";
                msg.sendToTarget();
                return;
            }

            int type = networkInfo.getType();
            //if default network is wifi, return directly
            if (type == ConnectivityManager.TYPE_WIFI ){
                Log.d(TAG, "process default network is WiFi");
                return;
            } else if (type == ConnectivityManager.TYPE_MOBILE ){
                Log.d(TAG, "process default network is GPRS");
                if (!networkInfo.isConnected()){
                    Message msg = handler.obtainMessage();
                    msg.what = Status.GPRS_DISCONNECTED;
                    msg.obj = "GPRS unconnected";
                    msg.sendToTarget();
                    return;
                }
            }

            //gprs connected
            Message msg = handler.obtainMessage();
            msg.what = Status.GPRS_CONNECTED;
            msg.obj = "GPRS is connected, download files";
            msg.sendToTarget();

            //start to do network operations,like download a file
            String urlStr="http://cn.archive.ubuntu.com/ubuntu/pool/universe/0/0ad-data/0ad-data-common_0.0.15-1_all.deb";
            String path="file";
            String fileName="0ad-data-common_0.0.15-1_all.deb";
            OutputStream output=null;
            try {
                /*
                 * 通过URL取得HttpURLConnection
                 * 要网络连接成功，需在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.INTERNET" />
                 */
                URL url=new URL(urlStr);
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                //取得inputStream，并将流中的信息写入SDCard

                /*
                 * 写前准备
                 * 1.在AndroidMainfest.xml中进行权限配置
                 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
                 * 取得写入SDCard的权限
                 * 2.取得SDCard的路径： Environment.getExternalStorageDirectory()
                 * 3.检查要保存的文件上是否已经存在
                 * 4.不存在，新建文件夹，新建文件
                 * 5.将input流中的信息写入SDCard
                 * 6.关闭流
                 */
                String SDCard= Environment.getExternalStorageDirectory()+"";
//                String pathName=SDCard+"/"+path+"/"+fileName + mDownloadCount;//文件存储路径
//
//                File file=new File(pathName);
//                InputStream input=conn.getInputStream();
//
//                String dir=SDCard+"/"+path;
//                new File(dir).mkdir(); //新建文件夹
//                file.createNewFile();  //新建文件

                String pathName= "/mnt/media_rw/FC2D-1646/"+ fileName + mDownloadCount;//文件存储路径
                Log.d(TAG, "pathName = " + pathName);

                File file=new File(pathName);
                InputStream input=conn.getInputStream();
                file.createNewFile();  //新建文件
                output=new FileOutputStream(file);
                //读取大文件
                byte[] buffer=new byte[4*1024];
                while(input.read(buffer)!=-1){
                    output.write(buffer);
                }
                output.flush();
                //    }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}



