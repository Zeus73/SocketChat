package com.zeus.socketchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zeus.socketchat.dataModels.ChatMsg;
import com.zeus.socketchat.dataModels.OtherUsersInfo;

import java.util.ArrayList;

/**
 * Broadcast receiver to receive the latest update on list of users registered
 * on the server and their online/offline status
 */
public class UsersListMsgReceiver extends BroadcastReceiver {
    public UsersListMsgReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("List Receiver indepe","here");
        Intent receiveMsgIntent=new Intent(context,ChatReceiveIntentService.class);
        context.startService(receiveMsgIntent);
        Client.friendsList.clear();


        ChatMsg tempMsg= (ChatMsg) intent.getSerializableExtra("userListChatMsg");
        ArrayList<OtherUsersInfo> tempList=tempMsg.usersList;

        for(int i=0;i<tempList.size();++i)
            if(!tempList.get(i).friendUsername.equals(Client.sender))
                Client.friendsList.add(tempList.get(i));


    }
}
