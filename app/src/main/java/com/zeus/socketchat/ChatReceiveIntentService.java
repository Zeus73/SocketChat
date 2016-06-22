package com.zeus.socketchat;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;


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
        if (intent != null) {

                try {
                    ByteBuffer buf=ByteBuffer.allocate(10240);
                    Client.clientSocketChannel.read(buf);
                    ChatMsg chatMsg=ChatMsg.deserialize(buf.array());
                    if(chatMsg!=null){
                        Intent msgRecBroadcastIntent=new Intent();
                        msgRecBroadcastIntent.setAction(ChatActivity.MsgReceiver.ACTION_RESP);
                        msgRecBroadcastIntent.putExtra("msg",chatMsg);
                        sendOrderedBroadcast(msgRecBroadcastIntent,null);
                    }


                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
        }


}
