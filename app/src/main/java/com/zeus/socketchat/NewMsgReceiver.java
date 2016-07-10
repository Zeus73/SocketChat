package com.zeus.socketchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zeus.socketchat.DataModels.ChatMsg;

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
//                Intent i=new Intent(context,ChatActivity.class);
//                i.putExtra("recipient",msgRec.sender);
//                i.putExtra("isPending",true);
//                i.putExtra("pending",msgRec.msgContent);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(i);
            }

        Intent receiveMsgIntent;
        receiveMsgIntent=new Intent(context,ChatReceiveIntentService.class);
        context.startService(receiveMsgIntent);

    }

}
