package com.quectel.asynctasktest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AsyncTaskTest extends AppCompatActivity {

    private Button button;
    private ProgressBar progressBar;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task_test);
        button = (Button)findViewById(R.id.button);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        textView = (TextView)findViewById(R.id.textView);



        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("klein", "onClick pid = " + Thread.currentThread().getId());
                ProgressBarAsyncTask asyncTask = new ProgressBarAsyncTask(textView, progressBar);
                asyncTask.execute(500);
            }
        });

    }

}
