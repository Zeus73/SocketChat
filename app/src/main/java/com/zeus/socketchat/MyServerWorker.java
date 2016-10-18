package com.zeus.socketchat;

import com.zeus.socketchat.dataModels.ChatMsg;
import com.zeus.socketchat.dataModels.OtherUsersInfo;
import com.zeus.socketchat.dataModels.PendingServerMsgs;
import com.zeus.socketchat.dataModels.UserDetails;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import static com.zeus.socketchat.NioServer.users;

/**
 * Created by Zeus on 15-Oct-16.
 */

public class MyServerWorker implements Runnable {

    NioServer.AcceptClient curClient;
    ChatMsg chatMsg;
    ByteBuffer wrappingBuffer;

    public MyServerWorker(NioServer.AcceptClient curClient, ChatMsg chatMsg) {
        this.curClient = curClient;
        this.chatMsg = chatMsg;
    }

    @Override
    public void run() {
        try{

        if(chatMsg.msgType==ChatMsg.CHAT){
            int i;
            for(i=0;i<users.size();++i){
                UserDetails curUser=users.get(i);
                if(curUser.username.equals(chatMsg.recipient)){
                    if(curUser.isUserOnline()){
                        SocketChannel recipientSocketChannel=curUser.socketChannel;
//
                        wrappingBuffer= ByteBuffer.wrap(ChatMsg.serialize(chatMsg));
                        while(wrappingBuffer.hasRemaining())
                            recipientSocketChannel.write(wrappingBuffer);

                    }else{
                        PendingServerMsgs newPendingMsg=new PendingServerMsgs(chatMsg);
                        newPendingMsg.save();
                    }
                    break;
                }
            }
            if(i==users.size()){
                ChatMsg retMsg=new ChatMsg(null, ChatMsg.NO_SUCH_USER, chatMsg.recipient, null, "No such user exists", null);
                wrappingBuffer=ByteBuffer.wrap(ChatMsg.serialize(retMsg));
                while(wrappingBuffer.hasRemaining())
                    curClient.socketChannel.write(wrappingBuffer);
            }
        }else if(chatMsg.msgType==ChatMsg.LIST_USERS){

            ArrayList<OtherUsersInfo> usersList=new ArrayList<>();
            for(int i=0;i<users.size();++i){
                OtherUsersInfo tempFriend=new OtherUsersInfo(users.get(i).username,users.get(i).isUserOnline());
                usersList.add(tempFriend);
            }

            ChatMsg retMsg=new ChatMsg(null, ChatMsg.LIST_USERS, chatMsg.sender, null, null, usersList);
            wrappingBuffer=ByteBuffer.wrap(ChatMsg.serialize(retMsg));
            while(wrappingBuffer.hasRemaining())
                curClient.socketChannel.write(wrappingBuffer);

        }else if(chatMsg.msgType==ChatMsg.LOGOUT){
            logoutTasks();
        }  // the while loop


    }catch (IOException e) {
        logoutTasks();
    }

        }


    private void logoutTasks(){
        if(curClient.socketChannel!=null)
            NioServer.myMap.remove(curClient.socketChannel);
        for(int i=0;i<users.size();++i){
            UserDetails curTraversal=users.get(i);
            if(curTraversal.username.equals(curClient.curUserInfo.username)){
//              curTraversal.isOnline=false;
                curTraversal.socketChannel=null;
                System.out.println("User "+curClient.curUserInfo.username+" Logged out");
                curClient.refreshUsersList();
                break;
            }
        }
    }
}
