package com.zeus.socketchat.myAsyncTasks;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Date;
import java.util.List;

/**
 * Created by Zeus on 7/23/2016.
 */
public class WifiConnectAsyncTask extends AsyncTask {

    WifiConnectAsyncTaskInterface listener;
    public interface WifiConnectAsyncTaskInterface{
        void tryWifiConnect(boolean status);
    }

    public void setListener(WifiConnectAsyncTaskInterface listener){
        this.listener=listener;
    }

    @Override
    protected void onPostExecute(Object o) {
        listener.tryWifiConnect((boolean)o);
    }

    WifiManager wifi;
    @Override
    protected Object doInBackground(Object[] params) {
        boolean state=false;
        Context context= (Context) params[0];
        String trgt= (String) params[1];
        wifi=(WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> list = wifi.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + trgt + "\"")) {
                state=wifi.enableNetwork(i.networkId, true);
            }else{
                wifi.disableNetwork(i.networkId);
            }
            wifi.reconnect();
        }


        WifiInfo curff=wifi.getConnectionInfo();
        SupplicantState hum=curff.getSupplicantState();
        NetworkInfo.DetailedState stg=WifiInfo.getDetailedStateOf(hum);

        Date curDate=new Date();
        long startTime= curDate.getTime();

        while(!stg.equals(NetworkInfo.DetailedState.CONNECTED)&&!stg.equals(NetworkInfo.DetailedState.OBTAINING_IPADDR)){
            curff=wifi.getConnectionInfo();
            hum=curff.getSupplicantState();
            stg=WifiInfo.getDetailedStateOf(hum);
            Log.i("hhfcncnc","st="+stg.toString());
            Date tempDate=new Date();
            long endTime=tempDate.getTime();
            if(endTime>=(startTime+10000))
                break;
        }

        if(curff.getSSID().equals("\"" + trgt + "\"")&&state&&(stg== NetworkInfo.DetailedState.CONNECTED||
                stg== NetworkInfo.DetailedState.OBTAINING_IPADDR)){
            Log.i("Wifi MY","coneected? true");
            return true;
        }
        Log.i("Wifi MY","coneected? false");
        return false;
    }

}
