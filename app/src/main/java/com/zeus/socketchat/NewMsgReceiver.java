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
        Log.i("truth","here");
        String msgRec=intent.getStringExtra("msg");
        StringTokenizer st=new StringTokenizer(msgRec);
        String byWhom=st.nextToken();
        byWhom=byWhom.substring(0,byWhom.length()-1);
        Intent i=new Intent(context,ChatActivity.class);
        i.putExtra("recipient",byWhom);
        i.putExtra("isPending",true);
        i.putExtra("pending",msgRec);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

}
