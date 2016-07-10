package com.zeus.socketchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zeus.socketchat.DataModels.ChatMsg;
import com.zeus.socketchat.DataModels.OtherUsersInfo;

import java.util.ArrayList;

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
