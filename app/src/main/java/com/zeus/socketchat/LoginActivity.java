package com.zeus.socketchat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements LoginAsyncTask.LoginAsyncTaskInterface {

    EditText userNameEditText;
    EditText passwordEditText;
    ProgressDialog progressDialog;
    SharedPreferences loginSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button loginButton;
        Button registerNewUserButton;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button changeServerIpButton;
        ConnectivityManager connMgr=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connMgr.getActiveNetworkInfo();
        if(networkInfo!=null&&networkInfo.isConnected()){
//            loginSharedPreferences=getSharedPreferences("login",Context.MODE_PRIVATE);
//            String usernameHistory=loginSharedPreferences.getString("username","");
//            if(!usernameHistory.equals("")){
//                String passwordHistory=loginSharedPreferences.getString("password","");
//                progressDialog = new ProgressDialog(this);
//                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                progressDialog.setMessage("Signing In ...");
//                progressDialog.show();
//                LoginAsyncTask loginAsyncTask=new LoginAsyncTask();
//                loginAsyncTask.SetLoginAsynctaskListener(LoginActivity.this);
//                loginAsyncTask.execute(usernameHistory,passwordHistory,false);
//            }
            changeServerIpButton= (Button) findViewById(R.id.serverIpChangeButton);
            changeServerIpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder bd=new AlertDialog.Builder(LoginActivity.this);
                    bd.setTitle("Set Server IP");
                    bd.setMessage("Enter new Server IP:");
                    final View serverDialogView=getLayoutInflater().inflate(R.layout.change_server_ip_dialog,null);
                    final EditText newIpEditText=(EditText) serverDialogView.findViewById(R.id.newServerIpEditText);
                    newIpEditText.setText(Client.IP);
                    bd.setView(serverDialogView);
                    bd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Client.IP=newIpEditText.getText().toString();
                        }
                    });
                    bd.setNeutralButton("Cancel", null);
                    bd.create().show();
                }
            });
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
                        progressDialog.setMessage("Registering ...");
                        progressDialog.show();
                        LoginAsyncTask loginAsyncTask=new LoginAsyncTask();
                        loginAsyncTask.SetLoginAsynctaskListener(LoginActivity.this);
                        loginAsyncTask.execute(username,password,true);

                    }
                });
            }
        }else{
            Toast.makeText(LoginActivity.this, "No Internet connectivity!", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onLoginAttempt(int authenticated) {
//        SharedPreferences.Editor editor=loginSharedPreferences.edit();
        progressDialog.dismiss();
        if(authenticated==0){
            Toast.makeText(LoginActivity.this, "Welcome: "+Client.sender, Toast.LENGTH_SHORT).show();
            Intent showUsersIntent=new Intent();
            showUsersIntent.setClass(LoginActivity.this,UsersListActivity.class);
            
//            editor.putString("username",userNameEditText.getText().toString());
//            editor.putString("password",passwordEditText.getText().toString());
//            editor.commit();

            startActivity(showUsersIntent);
        }else{
//            editor.putString("username","");
//            editor.putString("password","");
//            editor.commit();
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
