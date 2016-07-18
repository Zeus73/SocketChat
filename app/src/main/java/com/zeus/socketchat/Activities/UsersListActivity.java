package com.zeus.socketchat.Activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zeus.socketchat.Client;
import com.zeus.socketchat.DataModels.ChatMsg;
import com.zeus.socketchat.DataModels.OtherUsersInfo;
import com.zeus.socketchat.R;
import com.zeus.socketchat.MyAsyncTasks.SendMsgAsyncTask;
import com.zeus.socketchat.UsersListAdapter;
import java.util.ArrayList;

/**
 * The Activity displaying the list of users registered on the host server and their current  online/offline status
 * @author Aman Chandna
 */
    public class UsersListActivity extends AppCompatActivity {
    UsersListAdapter adapter;
    ListView usersListView;
    ArrayList<OtherUsersInfo> usersList;
    Button logoutButton;
    UsersListReceiver usersListReceiver;

    /**
     * Broadcast receiver to update the list of users and their online/offline status.
     */
    public class UsersListReceiver extends BroadcastReceiver{

        public static final String USER_LIST_ACTION="com.zeus.socketchat.intent.UPDATE_USER_LIST";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("Dependent Receiver","Success");
            ChatMsg tempMsg= (ChatMsg) intent.getSerializableExtra("userListChatMsg");
            ArrayList<OtherUsersInfo> tempList=tempMsg.usersList;
            usersList.clear();
            for(int i=0;i<tempList.size();++i)
                if(!tempList.get(i).friendUsername.equals(Client.sender))
                    usersList.add(tempList.get(i));
            adapter.notifyDataSetChanged();

        }
    }

    /**
     * logout the user when he exits this activity
     */
    @Override
    protected void onDestroy() {
        unregisterReceiver(usersListReceiver);
        logoutFunction();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        TextView tv1= (TextView) findViewById(R.id.usernameTV);
        tv1.setText("Welcome: "+Client.sender);
        this.usersList=new ArrayList<>();
        Log.i("populating with",Client.friendsList.size()+" users");
        for(int i=0;i<Client.friendsList.size();++i)
            this.usersList.add(Client.friendsList.get(i));

        adapter=new UsersListAdapter(UsersListActivity.this,usersList);
        logoutButton= (Button) findViewById(R.id.logoutButton);
        usersListView= (ListView) findViewById(R.id.usersListView);
        usersListView.setAdapter(adapter);

        usersListReceiver=new UsersListReceiver();
        IntentFilter intentFilter=new IntentFilter("com.zeus.socketchat.intent.UPDATE_USER_LIST");
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter.setPriority(5);
        registerReceiver(usersListReceiver,intentFilter);


        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutFunction();
                finish();
            }
        });

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String recipient=adapter.getItem(position).friendUsername;
                Intent startChatIntent=new Intent();
                startChatIntent.setClass(UsersListActivity.this,ChatActivity.class);
                startChatIntent.putExtra("recipient",recipient);
                startActivity(startChatIntent);
            }
        });

        SendMsgAsyncTask usersListFetchAsyncTask=new SendMsgAsyncTask();
        ChatMsg userListMsg=new ChatMsg(null,ChatMsg.LIST_USERS,Client.sender,null,null,null);
        usersListFetchAsyncTask.execute(userListMsg);
    }

    /**
     * This function notifies the server that the current user is logging out of the application
     */
    private void logoutFunction(){
        ChatMsg msg1=new ChatMsg(null,ChatMsg.LOGOUT,Client.sender,null,null,null);
        SendMsgAsyncTask logoutAsyncTask=new SendMsgAsyncTask();
        logoutAsyncTask.execute(msg1);
    }
}
