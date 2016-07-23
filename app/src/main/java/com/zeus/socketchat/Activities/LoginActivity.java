package com.zeus.socketchat.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.zeus.socketchat.ChatReceiveIntentService;
import com.zeus.socketchat.Client;
import com.zeus.socketchat.dataModels.MyWifiConfig;
import com.zeus.socketchat.dataModels.OtherUsersInfo;
import com.zeus.socketchat.myAsyncTasks.LoginAsyncTask;
import com.zeus.socketchat.NioServer;
import com.zeus.socketchat.R;
import com.zeus.socketchat.WifiApManager;

import java.util.List;

/**
 * It is the activity where a user registers or logins himself/herself into the chat server to access his/her messages.
 * @author Aman Chandna
 */
public class LoginActivity extends AppCompatActivity implements LoginAsyncTask.LoginAsyncTaskInterface {

    EditText userNameEditText;
    EditText passwordEditText;
    ProgressDialog progressDialog;
    Intent receiveMsgIntent;
    TextView WifiSsidTv;
    boolean isHost;

    /**
     * Stop the Host server if online when user exits the app
     * Restore the previous Wifi configuration for the user, both of hotspot and Wifi Scanner
     */
    @Override
    public void onBackPressed() {
        NioServer.serverIP=null;
        NioServer.toContinueServer=false;
        if(receiveMsgIntent!=null)
            stopService(receiveMsgIntent);
        new Delete().from(OtherUsersInfo.class).execute();
        if(Client.friendsList!=null)
            for(int i=0;i<Client.friendsList.size();++i){
                Client.friendsList.get(i).save();
            }
        if(isHost){
            WifiManager wifi=(WifiManager) getSystemService(Context.WIFI_SERVICE);
            WifiApManager tempManager=new WifiApManager(LoginActivity.this);
            WifiConfiguration setConfig=tempManager.getWifiApConfiguration();
            List<MyWifiConfig> configList=new Select().from(MyWifiConfig.class).execute();
            boolean startWifi=false;
            if(configList!=null&&configList.size()>0){
                MyWifiConfig jj=configList.get(0);
                setConfig=jj.wifiConfig;
                startWifi=jj.isWifiOn;
                Log.i("check::::","here+ "+configList.size());
            }
            tempManager.setWifiApEnabled(setConfig,false);
            wifi.setWifiEnabled(startWifi);
        }

        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button loginButton;
        Button registerNewUserButton;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        isHost=getIntent().getBooleanExtra("from",false);

        WifiSsidTv= (TextView) findViewById(R.id.wifiSsidTv);

        if(isHost){
            WifiSsidTv.setBackgroundColor(Color.GREEN);
            WifiSsidTv.setTextColor(Color.BLACK);
            WifiApManager wifiApManager=new WifiApManager(LoginActivity.this);
            WifiSsidTv.setText("HOTSPOT: "+wifiApManager.getWifiApConfiguration().SSID);
            TextView hotspotPasswordtv= (TextView) findViewById(R.id.hotspotPasswordTv);
            hotspotPasswordtv.setBackgroundColor(Color.BLACK);
            hotspotPasswordtv.setTextColor(Color.WHITE);
            hotspotPasswordtv.setText("PASSWORD: "+wifiApManager.getWifiApConfiguration().preSharedKey);
        }else{
            int netId=getIntent().getIntExtra("connect",0);
            WifiSsidTv.setBackgroundColor(Color.GREEN);
            WifiSsidTv.setTextColor(Color.BLACK);
            WifiManager wifiManager= (WifiManager) getSystemService(Context.WIFI_SERVICE);

            WifiSsidTv.setText("WIFI: "+wifiManager.getConnectionInfo().getSSID());
        }

                loginButton= (Button) findViewById(R.id.loginButton);
                registerNewUserButton= (Button) findViewById(R.id.registerNewUser);
                userNameEditText=(EditText) findViewById(R.id.usernameEditText);
                passwordEditText=(EditText) findViewById(R.id.passwordEditText);

                progressDialog = new ProgressDialog(this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


                if (loginButton != null) {
                    loginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String username=userNameEditText.getText().toString();
                            String password=passwordEditText.getText().toString();
                            if((!username.equals(""))&&(!password.equals(""))){

                                progressDialog.setMessage("Signing In ...");
                                progressDialog.show();
                                LoginAsyncTask loginAsyncTask=new LoginAsyncTask();
                                loginAsyncTask.SetLoginAsynctaskListener(LoginActivity.this);
                                loginAsyncTask.execute(username,password,false);
                            }else{
                                Toast.makeText(LoginActivity.this, "Please Enter Valid Username And Password", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                }
                if (registerNewUserButton != null) {
                    registerNewUserButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String username=userNameEditText.getText().toString();
                            String password=passwordEditText.getText().toString();
                            if((!username.equals(""))&&(!password.equals(""))){
                                progressDialog.setMessage("Registering ...");
                                progressDialog.show();
                                LoginAsyncTask loginAsyncTask=new LoginAsyncTask();
                                loginAsyncTask.SetLoginAsynctaskListener(LoginActivity.this);
                                loginAsyncTask.execute(username,password,true);
                            }else{
                                Toast.makeText(LoginActivity.this, "Please Enter Valid Username " +
                                        "And Password", Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                }

    }

    /**
     * receive the result from the server whether the registration/login attempt was successful
     * @param authenticated denotes the status of the login/register attempt made by the user
     */
    @Override
    public void onLoginAttempt(int authenticated) {

        if(authenticated!=19)
        progressDialog.dismiss();
        if(authenticated==0||authenticated==19){
            Toast.makeText(LoginActivity.this, "Welcome: "+Client.sender, Toast.LENGTH_SHORT).show();
            Intent showUsersIntent=new Intent();
            showUsersIntent.setClass(LoginActivity.this,UsersListActivity.class);
            if(authenticated==0){

            }else{
                Client.populateFriendListFromDatabase();
            }
            {
                receiveMsgIntent=new Intent(getApplication(),ChatReceiveIntentService.class);
                startService(receiveMsgIntent);
            }
            startActivity(showUsersIntent);
        }else{

            passwordEditText.setText("");
            userNameEditText.setText("");
            if(authenticated==1){
                Toast.makeText(LoginActivity.this, "Invalid credentials! Login failed", Toast.LENGTH_SHORT).show();
            }else if(authenticated==2){
                Toast.makeText(LoginActivity.this, "username already in use", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
