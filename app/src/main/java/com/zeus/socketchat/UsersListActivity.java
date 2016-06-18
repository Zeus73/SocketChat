package com.zeus.socketchat;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class UsersListActivity extends AppCompatActivity implements UserListAsyncTask.UserListAsyncTaskInterface {

    ArrayAdapter<String> adapter;
    ListView usersListView;
    ArrayList<String> usersList;
    Button refreshButton;
    Button logoutButton;

    @Override
    protected void onDestroy() {

        String logoutMsg=Client.sender+" logout";
        SendMsgAsyncTask logoutAsyncTask=new SendMsgAsyncTask();
        logoutAsyncTask.execute(logoutMsg);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        String logoutMsg=Client.sender+" logout";
        SendMsgAsyncTask logoutAsyncTask=new SendMsgAsyncTask();
        logoutAsyncTask.execute(logoutMsg);

        super.onBackPressed();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        TextView tv1= (TextView) findViewById(R.id.usernameTV);
        tv1.setText("Welcome: "+Client.sender);
        this.usersList=new ArrayList<>();
        adapter=new ArrayAdapter<String>(UsersListActivity.this,
                android.R.layout.simple_list_item_1,usersList);
        logoutButton= (Button) findViewById(R.id.logoutButton);
        usersListView= (ListView) findViewById(R.id.usersListView);
        usersListView.setAdapter(adapter);
        UserListAsyncTask userListAsyncTask=new UserListAsyncTask();
        userListAsyncTask.setUserListAsyncTaskListener(UsersListActivity.this);
        userListAsyncTask.execute();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences loginSharedPreferences=getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=loginSharedPreferences.edit();
                editor.putString("username","");
                editor.putString("password","");
                editor.commit();
                String logoutMsg=Client.sender+" logout";
                SendMsgAsyncTask logoutAsyncTask=new SendMsgAsyncTask();
                logoutAsyncTask.execute(logoutMsg);
                finish();
            }
        });

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String recipient=adapter.getItem(position);
                Intent startChatIntent=new Intent();
                startChatIntent.setClass(UsersListActivity.this,ChatActivity.class);
                startChatIntent.putExtra("recipient",recipient);
                startActivity(startChatIntent);
            }
        });

        refreshButton= (Button) findViewById(R.id.refreshUsersListButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserListAsyncTask refreshList=new UserListAsyncTask();
                refreshList.execute();
            }
        });

    }

    @Override
    public void onUserListFetch(ArrayList<String> usersList) {

//        this.usersList=usersList;
        this.usersList.clear();
        for(int i=0;i<usersList.size();++i){
            this.usersList.add(usersList.get(i));
        }
        adapter.notifyDataSetChanged();

    }
}
