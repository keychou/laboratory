package com.zhoukaihffoxmail.phoneinfotest;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoLte;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        getCellId();

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


        try {
            TelephonyManager mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            //通过反射，获取到PackageManager隐藏的方法getPackageSizeInfo()
            Method getPhoneCount = TelephonyManager.class.getMethod("getPhoneCount");
            int mPhoneCount = (int)getPhoneCount.invoke(mTm);
            Log.d(TAG, "mPhoneCount = " + mPhoneCount);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void getCellId(){
          TelephonyManager mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        List<NeighboringCellInfo> infos = mTm.getNeighboringCellInfo();
        Log.i(TAG, " infos:" + infos);
        StringBuffer sb = new StringBuffer("总数 : " + infos.size() + "\n");
        for (NeighboringCellInfo info1 : infos) { // 根据邻区总数进行循环
            sb.append(" LAC : " + info1.getLac()); // 取出当前邻区的LAC
            sb.append(" CID : " + info1.getCid()); // 取出当前邻区的CID
            sb.append(" BSSS : " + (-113 + 2 * info1.getRssi()) + "\n"); // 获取邻区基站信号强度
            Log.d(TAG, "cid" + info1.getCid() + ", nettype = " + info1.getNetworkType());
        }
        Log.i(TAG, " 获取邻区基站信息:" + sb.toString());

 /*       CdmaCellLocation location = (CdmaCellLocation) mTm.getCellLocation();
        int lac = location.getBaseStationId();
        Log.d(TAG, " lac = " + lac );*/

        StringBuilder str = new StringBuilder();
//获取小区信息
        List<CellInfo> cellInfoList = mTm.getAllCellInfo();
        str.append("小区信息:"+"\n");
        int index = 0;
        for (CellInfo cellInfo : cellInfoList) {
            //获取所有Lte网络信息
            if (cellInfo instanceof CellInfoLte) {
                str.append("[" + index + "]==CellInfoLte" + "\n");
                if (cellInfo.isRegistered()) {
                    str.append("isRegistered=YES" + "\n");
                }
//                str.append("TimeStamp:" + cellInfo.getTimeStamp() + "\n");
//                str.append(((CellInfoLte) cellInfo).getCellIdentity().toString() + "\n");
//                str.append(((CellInfoLte) cellInfo).getCellSignalStrength().toString() + "\n");

                CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
                CellIdentityLte cellIdentity = cellInfoLte.getCellIdentity();

                int longCid = cellIdentity.getCi();
                String cellidHex = DecToHex(longCid);
                Log.d(TAG, "cellidHex = " + cellidHex);
                String eNBHex = cellidHex.substring(0, cellidHex.length()-2);
                String cellIdHex = cellidHex.substring(cellidHex.length()-2, cellidHex.length());
                Log.d(TAG, "eNBHex = " + eNBHex + ", cellId = " + cellIdHex);

                int eNB = HexToDec(eNBHex);
                int cellId = HexToDec(cellIdHex);

                Log.d(TAG, "eNB = " + Integer.toString(eNB) + ", cellId = " + cellId);

            }
            //获取所有的cdma网络信息
            if (cellInfo instanceof CellInfoCdma) {
                str.append("[" + index + "]==CellInfoCdma" + "\n");
                if (cellInfo.isRegistered()) {
                    str.append("isRegistered=YES" + "\n");
                }
                str.append("TimeStamp:" + cellInfo.getTimeStamp() + "\n");
                str.append(((CellInfoCdma) cellInfo).getCellIdentity().toString() + "\n");
                str.append(((CellInfoCdma) cellInfo).getCellSignalStrength().toString() + "\n");
            }
        }
        Log.d(TAG, "str = " + str.toString());

//        StringBuilder str = new StringBuilder();
//        CellLocation location = mTm.getCellLocation();
//        if (location != null && location instanceof GsmCellLocation) {
//            GsmCellLocation l1 = (GsmCellLocation) location;
//            str.append("使用网络:" + "Gsm" + "\n");
//            str.append("cid"+l1.getCid()+ "\n");
//            str.append("lac"+l1.getLac()+ "\n");
//            str.append("Psc"+l1.getPsc()+ "\n");
//        } else if(location != null && location instanceof CdmaCellLocation){
//            CdmaCellLocation l2 = (CdmaCellLocation) location;
//            str.append(l2.toString() + "\n");
//        }
//        Log.d(TAG, "str = " + str.toString());
    }

    // Decimal -> hexadecimal
    public String DecToHex(int dec){
        return String.format("%x", dec);
    }

    // hex -> decimal
    public int HexToDec(String hex){
        return Integer.parseInt(hex, 16);
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
