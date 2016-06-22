package com.zeus.socketchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.StringTokenizer;

public class NewMsgReceiver extends BroadcastReceiver {
    public NewMsgReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.i("truth","here");
        ChatMsg msgRec= (ChatMsg) intent.getSerializableExtra("msg");
        if(msgRec!=null){
//            store in database
            if(!msgRec.Sender.equals(Client.currentlyChattingWith)){
                //do notification here
                setResultData(null);
                abortBroadcast();
            }else{
//                Intent i=new Intent(context,ChatActivity.class);
//                i.putExtra("recipient",msgRec.Sender);
//                i.putExtra("isPending",true);
//                i.putExtra("pending",msgRec.msgContent);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                context.startActivity(i);
            }

        }
        Intent receiveMsgIntent;
        receiveMsgIntent=new Intent(context,ChatReceiveIntentService.class);
        context.startService(receiveMsgIntent);

    }

}
