package com.quectel.usbdevicetest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.Iterator;

import static android.R.attr.data;

public class UsbDeviceTest extends AppCompatActivity {

    String TAG = "UsbDeviceTest";

    UsbDeviceEventReceiver mReceiver;

    Button list;

    UsbManager manager;

    UsbDevice device;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_device_test);

        list = (Button)findViewById(R.id.list);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager = (UsbManager) getSystemService(Context.USB_SERVICE);
                HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
                Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

                Log.d(TAG, "-------list all usb devices----------");
                while(deviceIterator.hasNext())
                {
                    device = deviceIterator.next();
                    Log.d(TAG, device.toString());
                }

                Log.d(TAG, "-------list completed----------");
            }
        });


        mReceiver = new UsbDeviceEventReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(mReceiver, filter);


    }

    class UsbDeviceEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

            Log.d(TAG, "UsbDevice = " + device.toString());
        }
    }
}
