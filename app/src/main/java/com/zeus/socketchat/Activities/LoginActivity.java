package com.zeus.socketchat.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.zeus.socketchat.ChatReceiveIntentService;
import com.zeus.socketchat.Client;
import com.zeus.socketchat.DataModels.OtherUsersInfo;
import com.zeus.socketchat.MyAsyncTasks.LoginAsyncTask;
import com.zeus.socketchat.NioServer;
import com.zeus.socketchat.R;

public class LoginActivity extends AppCompatActivity implements LoginAsyncTask.LoginAsyncTaskInterface {

    EditText userNameEditText;
    EditText passwordEditText;
    ProgressDialog progressDialog;
    SharedPreferences loginSharedPreferences;
    Intent receiveMsgIntent;


    public static final int SERVER_SETTINGS_REQUEST_CODE=1;

    @Override
    protected void onDestroy() {
//        NioServer.serverIP=null;
//        NioServer.toContinueServer=false;
        if(receiveMsgIntent!=null)
            stopService(receiveMsgIntent);
        new Delete().from(OtherUsersInfo.class).execute();
        if(Client.friendsList!=null)
        for(int i=0;i<Client.friendsList.size();++i){
            Client.friendsList.get(i).save();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.server_main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemClickedId=item.getItemId();
        if(menuItemClickedId==R.id.serverSettings){
            Intent serverSettingsIntent=new Intent(LoginActivity.this,ServerSettingsActivity.class);
//            startActivityForResult(serverSettingsIntent,SERVER_SETTINGS_REQUEST_CODE);
            startActivity(serverSettingsIntent);
        }else {
            Toast.makeText(LoginActivity.this, "Invalid choice", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Button loginButton;
        Button registerNewUserButton;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button changeServerIpButton;
//        ConnectivityManager connMgr=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo=connMgr.getActiveNetworkInfo();
//        if(networkInfo!=null&&networkInfo.isConnected()){



//            loginSharedPreferences=getSharedPreferences("login",Context.MODE_PRIVATE);
//            String usernameHistory=loginSharedPreferences.getString("username","");
//            String passwordHistory=loginSharedPreferences.getString("password","");
//            if(!usernameHistory.equals("")&&!passwordHistory.equals("")){
//               SetOnlineAsyncTask setOnlineAsyncTask=new SetOnlineAsyncTask();
//                setOnlineAsyncTask.execute(usernameHistory,passwordHistory,false);
//                onLoginAttempt(19);
//            }else{

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
//            }

//        }else{
//            Toast.makeText(LoginActivity.this, "No Internet connectivity!", Toast.LENGTH_SHORT).show();
//        }


    }

    @Override
    public void onLoginAttempt(int authenticated) {

//        SharedPreferences.Editor editor=loginSharedPreferences.edit();


        if(authenticated!=19)
        progressDialog.dismiss();
        if(authenticated==0||authenticated==19){
            Toast.makeText(LoginActivity.this, "Welcome: "+Client.sender, Toast.LENGTH_SHORT).show();
            Intent showUsersIntent=new Intent();
            showUsersIntent.setClass(LoginActivity.this,UsersListActivity.class);
            if(authenticated==0){
//                editor.putString("username",userNameEditText.getText().toString());
//                editor.putString("password",passwordEditText.getText().toString());
//                editor.commit();
            }else{
                Client.populateFriendListFromDatabase();
            }

            {
                receiveMsgIntent=new Intent(getApplication(),ChatReceiveIntentService.class);

                startService(receiveMsgIntent);
            }
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
