package com.zeus.socketchat;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class ChatReceiveIntentService extends IntentService {

    public ChatReceiveIntentService() {
        super("ChatReceiveIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("check1","Hello");
        if (intent != null) {

                try {
                    String rec=Client.din.readUTF();
                    Intent msgRecBroadcastIntent=new Intent();
                    msgRecBroadcastIntent.setAction(ChatActivity.MsgReceiver.ACTION_RESP);
                    msgRecBroadcastIntent.putExtra("msg",rec);
                    sendOrderedBroadcast(msgRecBroadcastIntent,null);
                } catch (IOException e) {
                    //do nothing
                }
            }
        }


}
