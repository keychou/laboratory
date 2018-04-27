package com.zhoukaihffoxmail.connectivitytest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

public class ConnectivityTest extends AppCompatActivity {
    public final String TAG = "klein";
    Button btStatus;
    TextView network_show, wifi_show, mobile_show;
    Switch aSwitch_wifi, aSwitch_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectivity_test);
        btStatus = (Button) findViewById(R.id.get_network_status);
        network_show = (TextView) findViewById(R.id.wifi_show);
        wifi_show = (TextView) findViewById(R.id.wifi_show);
        mobile_show = (TextView) findViewById(R.id.mobile_show);
        aSwitch_wifi = (Switch) findViewById(R.id.switch_wifi);
        aSwitch_data = (Switch) findViewById(R.id.switch_data);

        btStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isNetworkAvailable(getApplicationContext());
                isWiFi(getApplicationContext());
                isMobile(getApplicationContext());
            }
        });


        aSwitch_wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    ToggleWiFi(true);
                    Toast.makeText(getApplicationContext(), "WiFi已开启～",
                            Toast.LENGTH_LONG).show();
                } else {
                    ToggleWiFi(false);
                    Toast.makeText(getApplicationContext(), "WiFi已关闭!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        aSwitch_data.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    ToggleMobileData(ConnectivityTest.this, true);
                    Toast.makeText(getApplicationContext(), "数据连接已开启～",
                            Toast.LENGTH_LONG).show();
                } else {
                    ToggleMobileData(ConnectivityTest.this, false);
                    Toast.makeText(getApplicationContext(), "数据连接已关闭!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    //判断网络连接是否可用
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            System.out.println("CONNECTIVITY_SERVICE is not avaliable");
            network_show.setText("CONNECTIVITY_SERVICE is not avaliable");
        } else {
            Network[] networks = connectivityManager.getAllNetworks();
            if (networks != null && networks.length>0) {
                for (int i = 0; i < networks.length; i++) {
                    System.out.println("networks[" + i + "] = " + networks[i]);
                 //   if (networks[i].toString() == NetworkInfo.State.CONNECTED) {

                        System.out.println("networkInfo[i].getState() = " + networks[i].toString());
                        //network_show.setText("network connected");
                    //    return true;
                 //   }
                }
            }
        }
       // System.out.println("network disconnected");
      //  network_show.setText("network disconnected");
        return false;
    }

    //判断WiFi是否打开
    public boolean isWiFi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {

            boolean available = networkInfo.isAvailable();
            if(available){
                Log.i(TAG, "wifi availiable");
                wifi_show.setText("wifi connected, availiable");
            }
            else{
                Log.i(TAG, "wifi not availiable");
                wifi_show.setText("wifi connected, unavailiable");
            }
            return true;
        }
        wifi_show.setText("wifi disconnected");
        return false;
    }

    //判断移动数据是否打开
    public boolean isMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {

            boolean available = networkInfo.isAvailable();
            if(available){
                Log.i(TAG, "data availiable");
                mobile_show.setText("data connected, availiable");
            }
            else{
                Log.i(TAG, "data not availiable");
                mobile_show.setText("data connected, unavailiable");
            }

            return true;
        }
        mobile_show.setText("celluar network disconnected");
        return false;
    }



    public void ToggleWiFi(boolean status) {
        WifiManager wifiManager = (WifiManager) this
                .getSystemService(Context.WIFI_SERVICE);
        if (status == true && !wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        } else if (status == false && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    public void ToggleMobileData(Context context, boolean state) {
        ConnectivityManager connectivityManager = null;
        try {
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            Method method = connectivityManager.getClass().getMethod(
                    "setDataEnabled", new Class[] { boolean.class });
            method.invoke(connectivityManager, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
