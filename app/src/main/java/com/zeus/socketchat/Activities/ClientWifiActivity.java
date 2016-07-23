package com.zeus.socketchat.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.zeus.socketchat.R;
import com.zeus.socketchat.myAsyncTasks.WifiConnectAsyncTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Screen to choose which Host wifi to be connected to by the client
 * @author Aman Chandna
 */
public class ClientWifiActivity extends AppCompatActivity implements WifiConnectAsyncTask.WifiConnectAsyncTaskInterface {

    ProgressDialog progressDialog;
    ListView lv;
    WifiManager wifi;
    String wifis[];
    WifiScanReceiver wifiReciever;
    int netId;
    int oldNetId;
    boolean oldWifiState;
    EditText dtv;
    WifiConfiguration wifiConfiguration;
    String trgt;


    /**
     * restore the state of User's wifi to it's original condition and finish the activity
     */
    @Override
    public void onBackPressed() {
        wifi.setWifiEnabled(oldWifiState);
        if(oldWifiState){
            wifi.disableNetwork(netId);
            wifi.enableNetwork(oldNetId,true);
            wifi.reconnect();
        }

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
        if(oldWifiState)
            oldNetId=wifi.getConnectionInfo().getNetworkId();
        wifi.setWifiEnabled(true);
        lv=(ListView)findViewById(R.id.listView);
        wifiReciever = new WifiScanReceiver();
        wifi.startScan();


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                wifiConfiguration=new WifiConfiguration();
                wifiConfiguration.SSID=String.format("\"%s\"", parent.getAdapter().getItem(position));
                trgt=(String)parent.getAdapter().getItem(position);

                progressDialog = new ProgressDialog(ClientWifiActivity.this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("Authenticating Wifi ...");

                AlertDialog.Builder bd=new AlertDialog.Builder(ClientWifiActivity.this);
                bd.setTitle("Enter password");
                bd.setMessage("Ask for password from the host!");
                LayoutInflater inflater=getLayoutInflater();
                View v= inflater.inflate(R.layout.enter_password_dialog, null);
                dtv=(EditText) v.findViewById(R.id.wifiPasswordEditText);
                bd.setView(v);

                bd.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        progressDialog.show();
                        String pass=dtv.getText().toString();
                        pass="chat-"+pass;
                        wifiConfiguration.preSharedKey=String.format("\"%s\"", pass);
                        netId=wifi.addNetwork(wifiConfiguration);
                        WifiConnectAsyncTask wifiConnectAsyncTask
                                =new WifiConnectAsyncTask();
                        wifiConnectAsyncTask.setListener(ClientWifiActivity.this);
                        wifiConnectAsyncTask.execute(ClientWifiActivity.this,trgt);
                    }
                });

                bd.setNeutralButton("Cancel",null);
                bd.create().show();
            }
        });
    }

    @Override
    public void tryWifiConnect(boolean status) {
        progressDialog.dismiss();
        if(status){
            Intent i=new Intent(ClientWifiActivity.this,LoginActivity.class);
            startActivity(i);
        }else{
            Toast.makeText(ClientWifiActivity.this, "Incorrect Password :( Try again! ", Toast.LENGTH_SHORT).show();
        }
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
