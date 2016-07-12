package com.zeus.socketchat.Activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.zeus.socketchat.DataModels.MyWifiConfig;
import com.zeus.socketchat.MyAsyncTasks.StartHotspotAsyncTask;
import com.zeus.socketchat.NioServer;
import com.zeus.socketchat.R;
import com.zeus.socketchat.StartServerIntentService;
import com.zeus.socketchat.WIFI_AP_STATE;
import com.zeus.socketchat.WifiApManager;

import java.util.List;

public class StartingHostAnimationActivity extends AppCompatActivity implements StartHotspotAsyncTask.StartHotspotAsyncTaskInterface {

    WIFI_AP_STATE curApState;
    WifiApManager wifiApManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_host_animation);
        Context context=StartingHostAnimationActivity.this;
        wifiApManager=new WifiApManager(context);

        MyWifiConfig saveIt=new MyWifiConfig(wifiApManager.getWifiApConfiguration());

        new Delete().from(MyWifiConfig.class).execute();
        saveIt.save();
        StartHotspotAsyncTask startHotspotAsyncTask=new StartHotspotAsyncTask();
        startHotspotAsyncTask.setListener(StartingHostAnimationActivity.this);
        startHotspotAsyncTask.execute(context);

//        new Handler().postDelayed(new Runnable() {
//
//         /*
//          * Showing splash screen with a timer. This will be useful when you
//          * want to show case your app logo / company
//          */
//
//            @Override
//            public void run() {
//                // This method will be executed once the timer is over
//                // Start your app main activity
//
//            }
//        }, 4000);

    }

    @Override
    public void onHotspotStart(WIFI_AP_STATE curApState) {
        if(curApState==WIFI_AP_STATE.WIFI_AP_STATE_ENABLED){
            NioServer.toContinueServer=true;
            Intent startServerIntent=new Intent(StartingHostAnimationActivity.this,StartServerIntentService.class);
            startService(startServerIntent);
            Intent i=new Intent(StartingHostAnimationActivity.this,LoginActivity.class);
            i.putExtra("from",true);
//            WifiConfiguration wifiConfiguration=wifiApManager.getWifiApConfiguration();
//            Log.i("11111hotspot:","password: "+wifiConfiguration.preSharedKey);
//            Log.i("hotspot:","name: "+wifiConfiguration.SSID);
            startActivity(i);
            // close this activity

        }else{
            Toast.makeText(StartingHostAnimationActivity.this,"Hotspot creation failed",Toast.LENGTH_LONG);
            for(long i=0;i<999999999L;++i)
                ;
            WifiConfiguration setConfig=wifiApManager.getWifiApConfiguration();
            List<MyWifiConfig> configList=new Select().from(MyWifiConfig.class).execute();
            if(configList!=null&&configList.size()>0){
                setConfig=configList.get(0).wifiConfig;

            }
            wifiApManager.setWifiApEnabled(setConfig,false);

        }
//        NioServer.toContinueServer=false;
//        wifiApManager.setWifiApEnabled(wifiApManager.getWifiApConfiguration(),false);
        finish();
    }
}
