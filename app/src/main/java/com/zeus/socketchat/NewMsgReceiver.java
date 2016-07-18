package com.zeus.socketchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zeus.socketchat.DataModels.ChatMsg;

/**
 * Broadcast receiver to receive broadcast in case of new incoming chat messages
 * transmits the broadcast forward if the chat activity with the sender is currently active
 */
public class NewMsgReceiver extends BroadcastReceiver {
    public NewMsgReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ChatMsg msgRec= (ChatMsg) intent.getSerializableExtra("msg");

            msgRec.save();
            Log.i("new msg","+"+msgRec.msgContent);
            if(!msgRec.sender.equals(Client.currentlyChattingWith)){
                //do notification here
                setResultData(null);
                abortBroadcast();
            }else{
                //transmit the broadcast to a receive of lower priority and
                // display the message in the chat activity screen
            }

        Intent receiveMsgIntent;
        receiveMsgIntent=new Intent(context,ChatReceiveIntentService.class);
        context.startService(receiveMsgIntent);

    }

}
