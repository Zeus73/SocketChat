package com.zeus.socketchat;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.zeus.socketchat.activities.ChatActivity;
import com.zeus.socketchat.activities.UsersListActivity;
import com.zeus.socketchat.dataModels.ChatMsg;

import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * service to listen to the port for new incoming messages and to broadcast
 * the results for the appropriate broadcast receivers
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
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
                        if(chatMsg.msgType!=ChatMsg.LIST_USERS){
                            Intent msgRecBroadcastIntent=new Intent();
                            msgRecBroadcastIntent.setAction(ChatActivity.MsgReceiver.ACTION_RESP);
                            msgRecBroadcastIntent.putExtra("msg",chatMsg);
                            sendOrderedBroadcast(msgRecBroadcastIntent,null);
                        }else{
                            Log.i("Chat receive service",Client.sender+" received");
                            Intent usersListBroadcastIntent=new Intent();
                            usersListBroadcastIntent.setAction(UsersListActivity.UsersListReceiver.USER_LIST_ACTION);
                            usersListBroadcastIntent.putExtra("userListChatMsg",chatMsg);
                            sendOrderedBroadcast(usersListBroadcastIntent,null);
                        }

                    }


                } catch (IOException e) {
                    Log.i("ChatReceiveService","Corrupted message received");
                }
            }
        }


}
