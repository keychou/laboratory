package com.zhoukaihffoxmail.phoneinfotest;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.android.internal.telephony.ITelephony;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PhoneInfoTest extends AppCompatActivity {
    public static final String TAG = "PhoneInfoTest";

    Handler handler;
    Message message;
    ListView lvShowView;
    String[] statusNames;
    String[] callStatus;
    String[] dataNetworkTypes;
    String[] simCardStates;
    ArrayList<String> statusValues = new ArrayList<String>();
    String result;
    TelephonyManager mTm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_info_test);

        lvShowView = (ListView) findViewById(R.id.showView);

        mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTm.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);

       // ITelephony mTelephony = ITelephony.Stub.asInterface(ServiceManager.getService("phone"));

        statusNames = getResources().getStringArray(R.array.statusNames);
        callStatus = getResources().getStringArray(R.array.callStatus);
        dataNetworkTypes = getResources().getStringArray(R.array.dataNetworkTypes);
        simCardStates = getResources().getStringArray(R.array.simstate);


//        if (mTm.getLine1Number().isEmpty())
//        {
//            statusValues.add("unknown");
//        } else {
            statusValues.add(mTm.getLine1Number());
      //  }

        statusValues.add(mTm.getSubscriberId());

        statusValues.add(mTm.getDeviceId());
        statusValues.add(mTm.getSimSerialNumber());
        statusValues.add(callStatus[mTm.getCallState()]);
        statusValues.add(dataNetworkTypes[mTm.getNetworkType()]);
        statusValues.add(simCardStates[mTm.getSimState()]);
        statusValues.add(mTm.getSimOperator());
        statusValues.add(mTm.getSimCountryIso());
        statusValues.add(mTm.getSimOperatorName());
        statusValues.add(mTm.getVoiceMailNumber());


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String bt_mac = mBluetoothAdapter.getAddress();

        statusValues.add(bt_mac);



        ArrayList<Map<String, String>> status = new ArrayList<Map<String, String>>();

        for (int i = 0; i < statusValues.size(); i++ ){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("name", statusNames[i]);
            map.put("value", statusValues.get(i));
            status.add(map);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, statusValues);

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                status,
                R.layout.phone_info_item,
                new String[]{"name", "value"},
                new int[]{R.id.status_name, R.id.status_value});

        lvShowView.setAdapter(simpleAdapter);


         /* outgoing call
        1. callstate.connecting : prepare to establish connection with peer
        2. callstate.dialing : establish connection successfully, then dialing
        3. callstate.active : peer answer, now we are in the call
        4. callstate.disconnected : hang up
        */


        /* incoming call
        1. callstate.ringing : incoming call, ringing
        2. callstate.active : answer, now we are in the call
        3. callstate.disconnected : hang up
         */

        IntentFilter filter = new IntentFilter();
        filter.addAction("callstate.connecting");
        filter.addAction("callstate.dialing");
        filter.addAction("callstate.active");
        filter.addAction("callstate.cdma_active");   //special for cdma
        filter.addAction("callstate.ringing");
        filter.addAction("callstate.disconnected");

        String va[] = {"a", "b"};
        
        PhoneInfoTest.this.registerReceiver(mBroadcastReceiver, filter);
    }


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent) //onReceive函数不能做耗时的事情，参考值：10s以内
        {

            TelephonyManager mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            Log.d(TAG, "receive action="+ intent.getAction());
            if (intent.getAction() == "callstate.active" ){
                if (mTm.getSimOperatorName() == "46003") {
                    // do nothing

                }
            }
        }
    };


    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    result = "call state-----idle";
                    Log.d(TAG, result);
                 //   (new Exception()).printStackTrace();
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    result = "call state-----ringing "+incomingNumber;
                    Log.d(TAG, result);
                 //   (new Exception()).printStackTrace();

//                    try{
//                        Method getITelephonyMethod = mTm.getClass().getDeclaredMethod("getITelephony");
//                        getITelephonyMethod.setAccessible(true);
//                        ITelephony telephonyService = (ITelephony) getITelephonyMethod.invoke(mTm);
//                        telephonyService.silenceRinger();
//                        telephonyService.answerRingingCall();
//
//                    }catch (Exception e){
//                        e. );
//                    }


                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    result = "call state-----offhook ";
                    Log.d(TAG, result);
                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG);
                  //  (new Exception()).printStackTrace();
                    break;
                default:
                    break;
            }

            //super.onCallStateChanged(state, incomingNumber);
        }

    }


//    public class MyThreadTest extends Thread {
//
//        @Override
//        public void run()
//        {
//            Log.wtf(TAG, "klein--listen call");
//            Looper.prepare();
//            mTm = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
//            mTm.listen(new MyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
//            Looper.loop();
//        }
//    }


}
