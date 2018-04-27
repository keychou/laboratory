package com.quectel.asynctasktest;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by klein on 17-8-14.
 */

public class ProgressBarAsyncTask  extends AsyncTask<Integer, Integer, String> {

    private TextView textView;
    private ProgressBar progressBar;


    public ProgressBarAsyncTask(TextView textView, ProgressBar progressBar) {
        super();
        this.textView = textView;
        this.progressBar = progressBar;
    }


    /**
     * 这里的Integer参数对应AsyncTask中的第一个参数
     * 这里的String返回值对应AsyncTask的第三个参数
     * 该方法并不运行在UI线程当中，主要用于异步操作，所有在该方法中不能对UI当中的空间进行设置和修改
     * 但是可以调用publishProgress方法触发onProgressUpdate对UI进行操作
     */
    @Override
    protected String/*arg3*/ doInBackground(Integer... params/*arg1*/) {
        Log.d("klein", "doInBackground pid = " + Thread.currentThread().getId());
        NetOperator netOperator = new NetOperator();
        int i = 0;
        for (i = 10; i <= 100; i+=10) {
            netOperator.operator();
            publishProgress(i/*arg2*/);
            Log.d("klein", "i = " + i);
        }

        Log.d("klein", "params[0].intValue() = " + params[0].intValue());
        return i + params[0].intValue() + "";
    }


    /**
     * 这里的String参数对应AsyncTask中的第三个参数（也就是接收doInBackground的返回值）
     * 在doInBackground方法执行结束之后在运行，并且运行在UI线程当中 可以对UI空间进行设置
     */
    @Override
    protected void onPostExecute(String result/*arg3*/) {
        Log.d("klein", "onPostExecute pid = " + Thread.currentThread().getId());
        //(new Exception()).printStackTrace();
        textView.setText("异步操作执行结束" + result);
    }


    //该方法运行在UI线程当中,并且运行在UI线程当中 可以对UI空间进行设置
    @Override
    protected void onPreExecute() {
        Log.d("klein", "onPreExecute pid = " + Thread.currentThread().getId());
        //(new Exception()).printStackTrace();
        textView.setText("开始执行异步线程");
    }


    /**
     * 这里的Intege参数对应AsyncTask中的第二个参数
     * 在doInBackground方法当中，，每次调用publishProgress方法都会触发onProgressUpdate执行
     * onProgressUpdate是在UI线程中执行，所有可以对UI空间进行操作
     */
    @Override
    protected void onProgressUpdate(Integer... values/*arg2*/) {
        Log.d("klein", "onProgressUpdate pid = " + Thread.currentThread().getId());
        int vlaue = values[0];
        Log.d("klein", "vlaue = " + vlaue);
        //(new Exception()).printStackTrace();
        progressBar.setProgress(vlaue);
    }
}
