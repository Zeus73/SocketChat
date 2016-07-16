package com.zeus.socketchat;


import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.zeus.socketchat.DataModels.ChatMsg;
import com.zeus.socketchat.DataModels.InitialiseMsg;
import com.zeus.socketchat.DataModels.OtherUsersInfo;
import com.zeus.socketchat.DataModels.PendingServerMsgs;
import com.zeus.socketchat.DataModels.UserDetails;

import java.nio.*;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;


import java.io.IOException;
import java.net.*;

public class NioServer {
    static public String serverIP;
    public Vector<UserDetails> users;
    public static boolean toContinueServer=true;
    ArrayList<AcceptClient> threadList;
    ServerSocketChannel serverSocketChannel;

    public NioServer() {
        users=new Vector();
        List<UserDetails> tempList=new Select().from(UserDetails.class).execute();
        if(tempList!=null)
            for(int i=0;i<tempList.size();++i){
                UserDetails oldDetails=tempList.get(i);
//                oldDetails.isOnline=false;
                oldDetails.socketChannel=null;
                users.add(oldDetails);
            }

        threadList=new ArrayList<>();
        try {
            serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(Client.PORT));
            serverSocketChannel.configureBlocking(false);
            serverIP=getIpAddress();
            Client.IP=serverIP;
            System.out.println("The Server is Online! at: "+ serverIP);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("The Server failed to start");
        }


    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
//                        ip += "Server running at : "+
                        return inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";

        }
        return ip;
    }



    public void startNioServer(){
        try{
            threadList.clear();
            AcceptClient newClient;
            while(toContinueServer){
                SocketChannel socketChannel=serverSocketChannel.accept();
                if(socketChannel!=null){
                    System.out.println("New Client request!");
                    newClient=new AcceptClient(socketChannel);
                    threadList.add(newClient);
                }
            }

        }catch(IOException e){
            e.printStackTrace();
        }
        for(int i=0;i<threadList.size();++i){
            AcceptClient curThread=threadList.get(i);
            curThread.continueCurThread=false;
        }
        try {
            serverSocketChannel.close();
            serverIP=null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private class AcceptClient extends Thread{

        SocketChannel socketChannel;
        ByteBuffer buf,wrappingBuffer;
        String username;
        boolean continueCurThread=true;
        UserDetails curUserInfo;

        public AcceptClient(SocketChannel socketChannel){
            this.socketChannel=socketChannel;
            buf=ByteBuffer.allocate(10240);
            try {

                int bytesRead=socketChannel.read(buf);

                InitialiseMsg msg1=InitialiseMsg.deserialize(buf.array());
                buf.clear();
                username=msg1.username;
                if(msg1.isNewUser){
                    UserDetails curUserDetails= null;
                    try {
                        curUserDetails = new UserDetails(msg1.username, PasswordHash.createHash(msg1.password), socketChannel);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (InvalidKeySpecException e) {
                        e.printStackTrace();
                    }
                    curUserInfo=curUserDetails;
                    boolean registered=true;
                    for(int i=0;i<users.size();++i){
                        if(users.get(i).username.equals(msg1.username)){
                            registered=false;
                            break;
                        }

                    }
                    if(registered){
                        users.add(curUserDetails);
                        curUserDetails.save();
                        System.out.println("User Registered & Logged In: "+msg1.username);
                        wrappingBuffer=ByteBuffer.wrap(InitialiseMsg.serialize(new InitialiseMsg("true", null, true)));
                        while(wrappingBuffer.hasRemaining())
                            socketChannel.write(wrappingBuffer);
                        this.refreshUsersList();

                        start();
                    }else{
                        wrappingBuffer=ByteBuffer.wrap(InitialiseMsg.serialize(new InitialiseMsg("false", null, true)));
                        while(wrappingBuffer.hasRemaining())
                            socketChannel.write(wrappingBuffer);
                        socketChannel.close();
                    }


                }else{
                    int i;
                    for(i=0;i<users.size();++i){
                        try {
                            if((users.get(i).username.equals(msg1.username))&&(PasswordHash.validatePassword(msg1.password,users.get(i).password))){
                                System.out.println("Access granted: "+msg1.username);
                                curUserInfo=users.get(i);
                                wrappingBuffer=ByteBuffer.wrap(InitialiseMsg.serialize(new InitialiseMsg("true", null, false)));
                                while(wrappingBuffer.hasRemaining())
                                    socketChannel.write(wrappingBuffer);
                                users.get(i).socketChannel=socketChannel;
                                this.refreshUsersList();
                                start();
                                break;
                            }
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (InvalidKeySpecException e) {
                            e.printStackTrace();
                        }
                    }
                    if(i==users.size()){
                        System.out.println("Invalid Login attempt by "+msg1.username);
                        wrappingBuffer=ByteBuffer.wrap(InitialiseMsg.serialize(new InitialiseMsg("false", null, false)));
                        while(wrappingBuffer.hasRemaining())
                            socketChannel.write(wrappingBuffer);
                        socketChannel.close();

                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        void refreshUsersList(){
            ArrayList<OtherUsersInfo> usersList=new ArrayList<>();
            for(int i=0;i<users.size();++i){
                OtherUsersInfo tempFriend=new OtherUsersInfo(users.get(i).username,users.get(i).isUserOnline());
                usersList.add(tempFriend);
            }
            for(int i=0;i<users.size();++i){
                if(users.get(i).isUserOnline()){
                    SocketChannel tempSocketChannel=users.get(i).socketChannel;
                    ChatMsg retMsg=new ChatMsg(null, ChatMsg.LIST_USERS, users.get(i).username, null, null, usersList);
                    wrappingBuffer=ByteBuffer.wrap(ChatMsg.serialize(retMsg));

                    try {
                        while(wrappingBuffer.hasRemaining())
                            tempSocketChannel.write(wrappingBuffer);
                    } catch (IOException e) {
                        users.get(i).socketChannel=null;
                    }
                }else{
                    users.get(i).socketChannel=null;
                }
            }
        }

        public void run(){
            try {
                this.sleep(500,900);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            List<PendingServerMsgs> sendMsgsList=new Select().from(PendingServerMsgs.class)
                        .where("recipient=?",curUserInfo.username).execute();
            new Delete().from(PendingServerMsgs.class).where("recipient=?",curUserInfo.username).execute();
            int index=0;
            for(index=0;index<sendMsgsList.size();++index)
            {
                if(!curUserInfo.isUserOnline()){
                    continueCurThread=false;
                    break;
                }
                ChatMsg msg1=sendMsgsList.get(index).getChatMsg();
                System.out.println("sending-- "+msg1.msgContent);
                wrappingBuffer=ByteBuffer.wrap(ChatMsg.serialize(msg1));
                while(wrappingBuffer.hasRemaining())
                    try {
                        socketChannel.write(wrappingBuffer);
                    } catch (IOException e) {
                        continueCurThread=false;
                        break;
                    }

            }
            for(;index<sendMsgsList.size();++index)
                sendMsgsList.get(index).save();

            while(continueCurThread){
                buf.clear();
                System.out.println("accepted");
                try {
                    int shouldEnter=socketChannel.read(buf);
                    if(shouldEnter!=-1){

                        ChatMsg chatMsg=ChatMsg.deserialize(buf.array());
                        if(chatMsg.msgType==ChatMsg.CHAT){
                            int i;
                            for(i=0;i<users.size();++i){
                                UserDetails curUser=users.get(i);
                                if(curUser.username.equals(chatMsg.recipient)){
                                    if(curUser.isUserOnline()){
                                        SocketChannel recipientSocketChannel=curUser.socketChannel;
//
                                        wrappingBuffer=ByteBuffer.wrap(ChatMsg.serialize(chatMsg));
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
                                    socketChannel.write(wrappingBuffer);
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
                                socketChannel.write(wrappingBuffer);

                        }else if(chatMsg.msgType==ChatMsg.LOGOUT){

                            for(int i=0;i<users.size();++i){
                                UserDetails curTraversal=users.get(i);
                                if(curTraversal.username.equals(username)){
                                    curTraversal.socketChannel=null;
                                    System.out.println("User "+username+" Logged out");
                                    this.refreshUsersList();
                                    break;
                                }
                            }
                            break;		// Stop this thread
                        }
                    }  // the while loop


                } catch (IOException e) {
                    for(int i=0;i<users.size();++i){
                        UserDetails curTraversal=users.get(i);
                        if(curTraversal.username.equals(username)){
//                            curTraversal.isOnline=false;
                            curTraversal.socketChannel=null;
                            System.out.println("User "+username+" Logged out");
                            this.refreshUsersList();
                            break;
                        }
                    }
                    break; //close this thread
                }

            }

        } //end of run method

    }
}

