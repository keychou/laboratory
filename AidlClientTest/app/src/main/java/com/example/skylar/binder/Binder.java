package com.example.skylar.binder;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.skylar.aidlservice.ICat;

public class Binder extends AppCompatActivity {
    private ICat catService;
    private Button get;
    EditText color, weight;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            catService = ICat.Stub.asInterface(service);
            System.out.println("skylar1---Binder-----------catService = " + catService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            catService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder);

        get = (Button) findViewById(R.id.get);
        color = (EditText) findViewById(R.id.color);
        weight = (EditText) findViewById(R.id.weight);

        Intent intent = new Intent();
        intent.setAction("com.example.skylar.aidl.action.AIDL_SERVICE");
        intent.setPackage("com.example.skylar.aidlservice");
        bindService(intent, conn, Service.BIND_AUTO_CREATE);

        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    color.setText(catService.getColor());
                    weight.setText(catService.getWeight() + " ");
                } catch (RemoteException re) {
                    re.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_binder, menu);
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
