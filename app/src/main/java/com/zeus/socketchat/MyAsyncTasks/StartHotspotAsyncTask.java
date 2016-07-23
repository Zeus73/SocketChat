package com.zeus.socketchat.myAsyncTasks;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.AsyncTask;

import com.zeus.socketchat.WIFI_AP_STATE;
import com.zeus.socketchat.WifiApManager;

import java.util.Date;
import java.util.Random;

/**
 * The AsyncTask that performs the function of starting the hotspot on Host's device
 * Created by Zeus on 7/12/2016.
 */
public class StartHotspotAsyncTask extends AsyncTask {
   StartHotspotAsyncTaskInterface listener;
    public interface StartHotspotAsyncTaskInterface{
        void onHotspotStart(WIFI_AP_STATE curApState);
    }

    public void setListener(StartHotspotAsyncTaskInterface listener){
        this.listener=listener;
    }

    WifiApManager wifiApManager;
    WIFI_AP_STATE curApState;

    /**
     * This function notifies the activity implementing the StartHotspotAsyncTaskInterface when the hotspot is started
     * @param o receives the current Wifi state from DoInBackground
     */
    @Override
    protected void onPostExecute(Object o) {
        listener.onHotspotStart((WIFI_AP_STATE) o);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        Random randomGen=new Random();

        final Context context=(Context)params[0];
        wifiApManager = new WifiApManager(context);
        WifiConfiguration newConfig=new WifiConfiguration();
        newConfig.SSID="SocketHost"+(1000+randomGen.nextInt(100));
        newConfig.preSharedKey="chat-"+(5218+randomGen.nextInt(999));
        newConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        newConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

        wifiApManager.setWifiApEnabled(newConfig, true);
        curApState= wifiApManager.getWifiApState();
        Date curDate=new Date();
        long startTime= curDate.getTime();
        while(curApState!=WIFI_AP_STATE.WIFI_AP_STATE_ENABLED){
            curApState= wifiApManager.getWifiApState();
            Date tempDate=new Date();
            long endTime=tempDate.getTime();
            if(endTime>=(startTime+9000))
                break;
        }

        return curApState;
    }
}
