package com.zeus.socketchat.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zeus.socketchat.R;
import com.zeus.socketchat.WifiApManager;

public class MainActivity extends AppCompatActivity {

    Button startHotspotButton,startClientButton;
    WifiApManager wifiApManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startHotspotButton= (Button) findViewById(R.id.startHostButton);
        startHotspotButton.setBackgroundColor(getResources().getColor(android.R.color.black));
        startHotspotButton.setTextColor(getResources().getColor(android.R.color.white));
        startClientButton= (Button) findViewById(R.id.startClientButton);

        startHotspotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                WifiManager wifiManager = (WifiManager)
//                        getSystemService(Context.WIFI_SERVICE);
//                wifiManager.setWifiEnabled(false);
//                WifiApControl apControl = WifiApControl.getApControl(wifiManager);
//                if (apControl != null) {

                    // TURN OFF YOUR WIFI BEFORE ENABLE HOTSPOT
                    //if (isWifiOn(context) && isTurnToOn) {
                    //  turnOnOffWifi(context, false);
                    //}

//                    apControl.setWifiApEnabled(apControl.getWifiApConfiguration(),
//                            true);


                    Intent i=new Intent(MainActivity.this,StartingHostAnimationActivity.class);
                    startActivity(i);

//                    NioServer.toContinueServer=false;
//                    apControl.setWifiApEnabled(apControl.getWifiApConfiguration(),
//                            false);
//                    finish();
//                }
            }
        });

        startClientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,ClientWifiActivity.class);
                startActivity(i);
            }
        });
    }
}
