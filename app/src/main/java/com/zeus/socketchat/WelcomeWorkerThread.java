package com.zeus.socketchat;

import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.zeus.socketchat.dataModels.ChatMsg;
import com.zeus.socketchat.dataModels.PendingServerMsgs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by Zeus on 15-Oct-16.
 */

public class WelcomeWorkerThread implements Runnable {

    NioServer.AcceptClient curClient;
    ByteBuffer wrappingBuffer;

    public WelcomeWorkerThread(NioServer.AcceptClient curClient) {
        this.curClient = curClient;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500,900);
        } catch (InterruptedException e) {
            Log.i("AcceptClient","Thread was woken up prematurely");
        }

        List<PendingServerMsgs> sendMsgsList=new Select().from(PendingServerMsgs.class)
                .where("recipient=?",curClient.curUserInfo.username).execute();
        new Delete().from(PendingServerMsgs.class).where("recipient=?",curClient.curUserInfo.username).execute();
        int index=0;
        for(index=0;index<sendMsgsList.size();++index)
        {
            if(!curClient.curUserInfo.isUserOnline()){
                //continueCurThread=false;
                break;
            }
            ChatMsg msg1=sendMsgsList.get(index).getChatMsg();
            System.out.println("sending-- "+msg1.msgContent);
            wrappingBuffer= ByteBuffer.wrap(ChatMsg.serialize(msg1));
            while(wrappingBuffer.hasRemaining())
                try {
                    curClient.socketChannel.write(wrappingBuffer);
                } catch (IOException e) {
                    //continueCurThread=false;
                    break;
                }

        }
        for(;index<sendMsgsList.size();++index)
            sendMsgsList.get(index).save();
    }
}
