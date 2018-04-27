package com.quectel.wifitest;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;

public class WifiUtils {
    private WifiManager localWifiManager;
    //private List<ScanResult> wifiScanList;
    private List<WifiConfiguration> wifiConfigList;
    private WifiInfo wifiConnectedInfo;
    private WifiLock wifiLock;

    public WifiUtils( Context context){
        localWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    }


    public int WifiCheckState(){
        return localWifiManager.getWifiState();
    }


    public void WifiOpen(){
        if(!localWifiManager.isWifiEnabled()){
            localWifiManager.setWifiEnabled(true);
        }
    }


    public void WifiClose(){
        if(!localWifiManager.isWifiEnabled()){
            localWifiManager.setWifiEnabled(false);
        }
    }


    public void WifiStartScan(){
        localWifiManager.startScan();
    }


    public List<ScanResult> getScanResults(){
        return localWifiManager.getScanResults();
    }


    public List<String> scanResultToString(List<ScanResult> list){
        List<String> strReturnList = new ArrayList<String>();
        for(int i = 0; i < list.size(); i++){
            ScanResult strScan = list.get(i);
            String str = strScan.toString();
            boolean bool = strReturnList.add(str);
            if(!bool){
                Log.i("scanResultToSting","Addfail");
            }
        }
        return strReturnList;
    }


    public void getConfiguration(){
        wifiConfigList = localWifiManager.getConfiguredNetworks();
        for(int i =0;i<wifiConfigList.size();i++){
            Log.i("getConfiguration",wifiConfigList.get(i).SSID);
            Log.i("getConfiguration",String.valueOf(wifiConfigList.get(i).networkId));
        }
    }

    public int IsConfiguration(String SSID){
        Log.i("IsConfiguration",String.valueOf(wifiConfigList.size()));
        for(int i = 0; i < wifiConfigList.size(); i++){
            Log.i(wifiConfigList.get(i).SSID,String.valueOf( wifiConfigList.get(i).networkId));
            if(wifiConfigList.get(i).SSID.equals(SSID)){
                return wifiConfigList.get(i).networkId;
            }
        }
        return -1;
    }


    public int AddWifiConfig(List<ScanResult> wifiList,String ssid,String pwd){
        int wifiId = -1;
        for(int i = 0;i < wifiList.size(); i++){
            ScanResult wifi = wifiList.get(i);
            if(wifi.SSID.equals(ssid)){
                Log.i("AddWifiConfig","equals");
                WifiConfiguration wifiCong = new WifiConfiguration();
                wifiCong.SSID = "\""+wifi.SSID+"\"";
                wifiCong.preSharedKey = "\""+pwd+"\"";
                wifiCong.hiddenSSID = false;
                wifiCong.status = WifiConfiguration.Status.ENABLED;
                wifiId = localWifiManager.addNetwork(wifiCong);
                if(wifiId != -1){
                    return wifiId;
                }
            }
        }
        return wifiId;
    }


    public boolean ConnectWifi(int wifiId){
        for(int i = 0; i < wifiConfigList.size(); i++){
            WifiConfiguration wifi = wifiConfigList.get(i);
            if(wifi.networkId == wifiId){
                while(!(localWifiManager.enableNetwork(wifiId, true))){
                    Log.i("ConnectWifi",String.valueOf(wifiConfigList.get(wifiId).status));
                }
                return true;
            }
        }
        return false;
    }


    public void createWifiLock(String lockName){
        wifiLock = localWifiManager.createWifiLock(lockName);
    }


    public void acquireWifiLock(){
        wifiLock.acquire();
    }


    public void releaseWifiLock(){
        if(wifiLock.isHeld()){
            wifiLock.release();
        }
    }


    public void getConnectedInfo(){
        wifiConnectedInfo = localWifiManager.getConnectionInfo();
    }

    public String getConnectedMacAddr(){
        return (wifiConnectedInfo == null)? "NULL":wifiConnectedInfo.getMacAddress();
    }


    public String getConnectedSSID(){
        return (wifiConnectedInfo == null)? "NULL":wifiConnectedInfo.getSSID();
    }


    public int getConnectedIPAddr(){
        return (wifiConnectedInfo == null)? 0:wifiConnectedInfo.getIpAddress();
    }


    public int getConnectedID(){
        return (wifiConnectedInfo == null)? 0:wifiConnectedInfo.getNetworkId();
    }
}

