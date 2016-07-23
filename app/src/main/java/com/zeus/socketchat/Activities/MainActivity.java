package com.zeus.socketchat.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zeus.socketchat.R;

/**
 * MainActivity is the first screen of the App where the user chooses to continue as a chat host or chat client
 * The chat host creates a hotspot for other clients to be connected
 * the Wifi of the the clients is turned on so that they can connect to the host's Wifi
 * @author Aman Chandna
 */
public class MainActivity extends AppCompatActivity {

    Button startHotspotButton,startClientButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startHotspotButton= (Button) findViewById(R.id.startHostButton);
        startClientButton= (Button) findViewById(R.id.startClientButton);

        startHotspotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,StartingHostAnimationActivity.class);
                startActivity(i);
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
