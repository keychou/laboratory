package com.test.ThreadTest;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.test.myapplication.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class ThreadTest extends Activity {

    Button start_thread;
    Button start_runnable;
    Button start_callable;
    Button start_AsyncTask;
    Button start_timetask;
    Button start_excutor;

    TextView textView;
    TextView textView2;
    private ProgressBar progressBar;
    private boolean quit;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Thread.currentThread().setName("main_thread");

        
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        start_thread = (Button) findViewById(R.id.start_thread);
        start_runnable = (Button) findViewById(R.id.start_runnable);
        start_callable = (Button) findViewById(R.id.start_callable);
        start_AsyncTask = (Button) findViewById(R.id.start_AsyncTask);
        start_timetask = (Button) findViewById(R.id.start_timetask);
        start_excutor = (Button) findViewById(R.id.start_excutor);


        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();

        start_thread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyThreadTest().start();
            }
        });

        start_runnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RunnableTest st = new RunnableTest();
                new Thread(st, "runnable test").start();
            }
        });

        start_callable.setOnClickListener(new View.OnClickListener()
        {
             @Override
             public void onClick(View view) {
                 CallableTest ct = new CallableTest();
                 FutureTask<Integer> task = new FutureTask<Integer>(ct);
                 new Thread(task, "callable test").start();
             }
        });

        start_AsyncTask.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                AsyncTaskTest asyncTask = new AsyncTaskTest(textView, progressBar);
                asyncTask.execute(1000);
            }
        });

        start_timetask.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Thread.currentThread().setName("time_task");
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
//                        textView2.setText("time");
//                        try
//                        {
//                            Thread.currentThread().sleep(2);
//                        }catch(InterruptedException e)
//                        {
//
//                        }
//                        textView2.setText("task");
                        System.out.println("time task");
                    }
                }, 0, 1200);
            }
        });


        start_excutor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Thread.currentThread().setName("excutor");

                //使用Executors类的newCachedThreadPool()方法创建新的 Executor 对象。
                ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors. newCachedThreadPool();


                //创建并提交10个 Task 对象给执行者。用随机数字初始化任务。
                for (int i=0; i<10; i++) {
                    RunnableTest task=new RunnableTest();
                    executor.submit(task);
                }

                //创建迭代为5的for循环。在每步，传递执行者调用 showLog() 方法写相关信息，并让线程休眠1秒。
                for (int i=0; i<5; i++){
                    showLog(executor);
                    try
                    {
                        TimeUnit.SECONDS.sleep(1);
                    }catch (InterruptedException e)
                    {

                    }
                }

                //使用 shutdown() 方法关闭执行者。
                executor.shutdown();


                //创建迭代为5的for循环。在每步，传递执行者调用 showLog() 方法写相关信息，并让线程休眠1秒。
                for (int i=0; i<5; i++){
                    showLog(executor);
                    try
                    {
                        TimeUnit.SECONDS.sleep(1);
                    }catch (InterruptedException e)
                    {

                    }
                }

                //显示一条结束程序的信息。
                System.out.printf("Main: End of the program.\n");
            }
        });
    }

    public class MyThreadTest extends Thread {

        MyThreadTest()
        {
            setName("Thread Test");
        }

        @Override
        public void run()
        {

            while(true)
            {
                System.out.println("new thread test");
                try
                {
                    Thread.sleep(100);
                }catch (InterruptedException e)
                {

                }
            }
        }
    }

    // 创建一个类，名为 RunnableTest，并实现 Runnable 接口.
    public class RunnableTest implements Runnable
    {

        public void run()
        {
//            while(true)
//            {
                System.out.println("new Runnable test");
                try
                {
                    Thread.sleep(1000);
                }catch (InterruptedException e)
                {

                }
//            }
        }

    }

    public class CallableTest implements Callable<Integer>
    {
        public Integer call()
        {
            int count = 100;
            for (int i = 0; i < count; i++)
            {
                System.out.println("new callable test");
                try
                {
                    Thread.sleep(1000);
                }catch (InterruptedException e)
                {

                }
            }
            return 1;
        }
    }

    public class AsyncTaskTest extends AsyncTask<Integer, Integer, String>
    {
        private ProgressBar progressBar;
        private TextView textView;

        AsyncTaskTest(TextView textView, ProgressBar progressBar) {
            super();
            this.textView = textView;
            this.progressBar = progressBar;
        }


        @Override
        protected String doInBackground(Integer... params) {
            int count = 50;
            for (Integer i = 0; i < count; i++)
            {

                publishProgress(i);
                try
                {
                    Thread.sleep(1000);
                }catch (InterruptedException e)
                {

                }
            }
            return "ok~";
        }



        @Override
        protected void onPostExecute(String result) {
            textView.setText("async task finish " + result);
        }

        @Override
        protected void onPreExecute() {
            textView.setText("start async task ");
        }

        @Override
        protected void onProgressUpdate(Integer... process) {
            Integer process1 = process[0];
            textView.setText("process " + process1.toString());
            progressBar.setProgress(process[0]);
//            progressBar.incrementProgressBy(process[0]);
        }
    }


//    //1. 创建一个类，名为 Task，并实现 Runnable 接口.
//    public class Task implements Runnable {
//        //2. 声明一个私有 long 属性，名为 milliseconds.
//        private long milliseconds;
//
//        //3. 实现类的构造函数，初始化它的属性。
//        public Task(long milliseconds) {
//            this.milliseconds = milliseconds;
//        }
//
//
//        //4. 实现 run() 方法。通过 milliseconds 属性让线程进入一段时间休眠。
//        @Override
//        public void run() {
//            System.out.printf("%s: Begin\n", Thread.currentThread().getName());
//            try {
//                TimeUnit.MILLISECONDS.sleep(milliseconds);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.printf("%s: End\n", Thread.currentThread().getName());
//        }
//    }

    //实现 showLog() 方法，接收 Executor 作为参数。写关于pool的大小，任务的数量，和执行者状态的信息。
    private static void showLog(ThreadPoolExecutor executor) {
        System.out.printf("*********************\n");
        System.out.printf("Main: Executor Log\n");
        System.out.printf("Main: Executor: Core Pool Size:%d\n",executor.getCorePoolSize());
        System.out.printf("Main: Executor: Pool Size: %d\n",executor. getPoolSize());
        System.out.printf("Main: Executor: Active Count:%d\n",executor.getActiveCount());
        System.out.printf("Main: Executor: Task Count: %d\n",executor. getTaskCount());
        System.out.printf("Main: Executor: Completed Task Count:%d\n",executor.getCompletedTaskCount());
        System.out.printf("Main: Executor: Shutdown: %s\n",executor. isShutdown());
        System.out.printf("Main: Executor: Terminating:%s\n",executor.isTerminating());
        System.out.printf("Main: Executor: Terminated: %s\n",executor. isTerminated());
        System.out.printf("*********************\n");
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
