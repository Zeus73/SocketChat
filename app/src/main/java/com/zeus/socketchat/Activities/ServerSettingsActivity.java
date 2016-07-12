package com.zeus.socketchat.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zeus.socketchat.NioServer;
import com.zeus.socketchat.R;
import com.zeus.socketchat.StartServerIntentService;

public class ServerSettingsActivity extends AppCompatActivity {

    NioServer nioServer;
    Button startServerButton,stopServerButton,IPRefresh;
    TextView onlineServerIPTV;
    Intent startServerIntent;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_settings);

        IPRefresh=(Button) findViewById(R.id.IPRefresh);
        IPRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onlineServerIPTV.setText(NioServer.serverIP);
            }
        });
        onlineServerIPTV= (TextView) findViewById(R.id.onlineServerIPTV);
        stopServerButton= (Button) findViewById(R.id.stopServerButton);
        startServerButton= (Button) findViewById(R.id.startServerButton);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        startServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopServerButton.setClickable(true);
                startServerButton.setClickable(false);
//                ServerStartAsyncTask serverStartAsyncTask=new ServerStartAsyncTask();
////                serverStartAsyncTask.setServerStartedListener(ServerSettingsActivity.this);
//                serverStartAsyncTask.execute();
                NioServer.toContinueServer=true;
                startServerIntent=new Intent(ServerSettingsActivity.this,StartServerIntentService.class);
                startService(startServerIntent);
                onlineServerIPTV.setText("Server is starting up!");
//                progressDialog.setMessage("Starting Server...");
//                progressDialog.show();
//                for(int i=0;i<99999;++i)
//                    for(int j=0;j<99999;++j)
//                        for(int k=0;k<99999;++k)
//                    ;
//                progressDialog.dismiss();
                onlineServerIPTV.setText("Server live on: "+NioServer.serverIP);

//                Thread newThread=new Thread(new IPTVRefresher());
//                newThread.start();


            }
        });


        stopServerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NioServer.toContinueServer=false;
                onlineServerIPTV.setText("No server Online");
                startServerButton.setClickable(true);
                stopServerButton.setClickable(false);
            }
        });

        if(NioServer.toContinueServer&&NioServer.serverIP!=null){
            onlineServerIPTV.setText(NioServer.serverIP);
            stopServerButton.setClickable(true);
            startServerButton.setClickable(false);
        }else{
            onlineServerIPTV.setText("No server Online");
            stopServerButton.setClickable(false);
            startServerButton.setClickable(true);
        }

    }

//    private class IPTVRefresher implements Runnable{
//
//        @Override
//        public void run() {
//            while(NioServer.serverIP==null)
//                ;
////            onlineServerIPTV.setText("Server live on: "+NioServer.serverIP);
//        }
//    }

}
