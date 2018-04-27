package com.quectel.wifitest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class WifiTest extends AppCompatActivity {

    private String wifiPassword = null;
    private Button wifiSearchButton;
    private WifiUtils localWifiUtils;
    private List<ScanResult> wifiResultList;
    private List<String> wifiListString = new ArrayList<String>();
    private ListView wifiSearchListView;
    private ArrayAdapter<String> arrayWifiAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_test);


        wifiSearchButton = (Button)findViewById(R.id.wifiSearchButton);
        WIFIButtonListener wifiButtonListener = new WIFIButtonListener();
        wifiSearchButton.setOnClickListener(wifiButtonListener);
        wifiSearchListView = (ListView)findViewById(R.id.wifiListView);
        arrayWifiAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,wifiListString);
        wifiSearchListView.setAdapter(arrayWifiAdapter);
        ListOnItemClickListener wifiListListener = new ListOnItemClickListener();
        wifiSearchListView.setOnItemClickListener(wifiListListener);

        localWifiUtils = new WifiUtils(WifiTest.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.control_pcmain, menu);
        return true;
    }


    class WIFIButtonListener implements OnClickListener{
        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub
            wifiListString.clear();
            localWifiUtils.WifiOpen();
            localWifiUtils.WifiStartScan();
            //0正在关闭,1WIFi不可用,2正在打开,3可用,4状态不可zhi
            while(localWifiUtils.WifiCheckState() != WifiManager.WIFI_STATE_ENABLED){//等待Wifi开启
                Log.i("WifiState",String.valueOf(localWifiUtils.WifiCheckState()));
            }
            try {
                Thread.sleep(3000);//休眠3s，不休眠则会在程序首次开启WIFI时候，处理getScanResults结果，wifiResultList.size()发生异常
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            wifiResultList = localWifiUtils.getScanResults();
            localWifiUtils.getConfiguration();
            if(wifiListString != null){
                Log.i("WIFIButtonListener","dataChange");
                scanResultToString(wifiResultList,wifiListString);
            }
        }
    }

    class ListOnItemClickListener implements OnItemClickListener{
        String wifiItemSSID = null;
        private View selectedItem;
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            // TODO Auto-generated method stub
            Log.i("ListOnItemClickListener","start");
            selectedItem = arg1;
            arg1.setBackgroundResource(R.color.gray);//点击的Item项背景设置
            String wifiItem = arrayWifiAdapter.getItem(arg2);//获得选中的设备
            String []ItemValue = wifiItem.split("--");
            wifiItemSSID = ItemValue[0];
            Log.i("ListOnItemClickListener",wifiItemSSID);
            int wifiItemId = localWifiUtils.IsConfiguration("\""+wifiItemSSID+"\"");
            Log.i("ListOnItemClickListener",String.valueOf(wifiItemId));
            if(wifiItemId != -1){
                if(localWifiUtils.ConnectWifi(wifiItemId)){//连接指定WIFI
                    arg1.setBackgroundResource(R.color.green);
                }
            }
            else{//没有配置好信息，配置
                WifiPswDialog pswDialog = new WifiPswDialog(WifiTest.this,new WifiPswDialog.OnCustomDialogListener() {
                    @Override
                    public void back(String str) {
                        // TODO Auto-generated method stub
                        wifiPassword = str;
                        if(wifiPassword != null){
                            int netId = localWifiUtils.AddWifiConfig(wifiResultList,wifiItemSSID, wifiPassword);
                            Log.i("WifiPswDialog",String.valueOf(netId));
                            if(netId != -1){
                                localWifiUtils.getConfiguration();//添加了配置信息，要重新得到配置信息
                                if(localWifiUtils.ConnectWifi(netId)){
                                    selectedItem.setBackgroundResource(R.color.green);
                                }
                            }
                            else{
                                Toast.makeText(WifiTest.this, "connect error", Toast.LENGTH_SHORT).show();
                                selectedItem.setBackgroundResource(R.color.burlywood);
                            }
                        }
                        else{
                            selectedItem.setBackgroundResource(R.color.burlywood);
                        }
                    }
                });
                pswDialog.show();
            }
        }
    }
    //ScanResult类型转为String
    public void scanResultToString(List<ScanResult> listScan,List<String> listStr){
        for(int i = 0; i <listScan.size(); i++){
            ScanResult strScan = listScan.get(i);
            String str = strScan.SSID+"--"+strScan.BSSID;
            boolean bool = listStr.add(str);
            if(bool){
                arrayWifiAdapter.notifyDataSetChanged();//数据更新,只能单个Item更新，不能够整体List更新
            }
            else{
                Log.i("scanResultToSting","fail");
            }
            Log.i("scanResultToString",listStr.get(i));
        }
    }
	/*private BroadcastReceiver WifiBroadcastRec = new BroadcastReceiver(){
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String wifiAction = intent.getAction();
		if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(wifiAction)){
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_ENABLED);
			if(wifiState == WifiManager.WIFI_STATE_ENABLED){
				try {
					Thread.sleep(3000);//休眠3s，不休眠则会在程序首次开启WIFI时候，处理getScanResults结果，wifiResultList.size()发生异常
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				wifiResultList = localWifiUtils.getScanResults();
				localWifiUtils.getConfiguration();
				if(wifiListString != null){
					scanResultToString(wifiResultList,wifiListString);
				}
			}
		}
	  }
	};*/
}
