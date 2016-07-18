package com.zeus.socketchat.Activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.zeus.socketchat.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen to choose which Host wifi to be connected to by the client
 * @author Aman Chandna
 */
public class ClientWifiActivity extends AppCompatActivity {

    ListView lv;
    WifiManager wifi;
    String wifis[];
    WifiScanReceiver wifiReciever;
    int netId;

    boolean oldWifiState;

    /**
     * restore the state of User's wifi to it's original condition and finish the activity
     */
    @Override
    public void onBackPressed() {
        wifi.setWifiEnabled(oldWifiState);
        super.onBackPressed();
    }

    /**
     * unregister the wifi scanner receiver when the activity goes in background
     */
    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    /**
     * Reregister the wifi scanner receiver when the activity goes in background
     */
    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_wifi);

        wifi=(WifiManager) getSystemService(Context.WIFI_SERVICE);

        oldWifiState=wifi.isWifiEnabled();
        wifi.setWifiEnabled(true);
        lv=(ListView)findViewById(R.id.listView);
        wifiReciever = new WifiScanReceiver();
        wifi.startScan();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final WifiConfiguration wifiConfiguration=new WifiConfiguration();
                wifiConfiguration.SSID=String.format("\"%s\"", parent.getAdapter().getItem(position));


                AlertDialog.Builder bd=new AlertDialog.Builder(ClientWifiActivity.this);
                bd.setTitle("Enter password");
                bd.setMessage("Ask for password from the host!");
                LayoutInflater inflater=getLayoutInflater();
                View v= inflater.inflate(R.layout.enter_password_dialog, null);
                final EditText dtv=(EditText) v.findViewById(R.id.wifiPasswordEditText);
                bd.setView(v);

                bd.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String pass=dtv.getText().toString();
                        pass="chat-"+pass;
                        wifiConfiguration.preSharedKey=String.format("\"%s\"", pass);
                        netId=wifi.addNetwork(wifiConfiguration);
                        wifi.disconnect();
                        wifi.enableNetwork(netId,true);
                        wifi.reconnect();
                        Intent i=new Intent(ClientWifiActivity.this,LoginActivity.class);
                        startActivity(i);
                    }
                });
                bd.setNeutralButton("Cancel",null);
                bd.create().show();
            }
        });
    }


    /**
     * Broadcast Receiver to notify the apdapter regarding the change in list of available Wifi, if any
     */
    private class WifiScanReceiver extends BroadcastReceiver {
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifi.getScanResults();
            List<String> tempList=new ArrayList<>();
            for(int i = 0; i < wifiScanList.size(); i++){
                String check=wifiScanList.get(i).SSID;
                if(check.length()>=10&&check.substring(0,10).equals("SocketHost"))
                    tempList.add(check);
            }

            wifis = new String[tempList.size()];
            for(int i=0;i<tempList.size();++i)
                wifis[i]=tempList.get(i);

            lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,wifis));
        }
    }
}
