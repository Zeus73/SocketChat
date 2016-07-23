package com.zeus.socketchat;


import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.zeus.socketchat.dataModels.ChatMsg;
import com.zeus.socketchat.dataModels.InitialiseMsg;
import com.zeus.socketchat.dataModels.OtherUsersInfo;
import com.zeus.socketchat.dataModels.PendingServerMsgs;
import com.zeus.socketchat.dataModels.UserDetails;

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

/**
 * The class that store the server details, list of registered users, list of ongoing threads
 */
public class NioServer {
    static public String serverIP;
    public Vector<UserDetails> users;
    public static boolean toContinueServer=true;
    ArrayList<AcceptClient> threadList;
    ServerSocketChannel serverSocketChannel;

    /**
     * Constructor to create an instance of the NioServer class
     */
    public NioServer() {
        users=new Vector();
        List<UserDetails> tempList=new Select().from(UserDetails.class).execute();
        if(tempList!=null)
            for(int i=0;i<tempList.size();++i){
                UserDetails oldDetails=tempList.get(i);
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
            e.printStackTrace();
            ip += "The required socket was closed unexpectedly! " + e.toString() + "\n";
            return null;
        }
        return ip;
    }


    /**
     * function to initiate the servver to listen to the specified port and
     * accept incoming client connection requests
     */
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
            for(int i=0;i<threadList.size();++i){
                AcceptClient curThread=threadList.get(i);
                curThread.continueCurThread=false;
            }
            serverSocketChannel.close();
            serverIP=null;

        }catch(IOException e){
            serverSocketChannel=null;
            serverIP=null;
            return;
        }
    }


    /**
     * class extending thread to listen to a specified SocketChannel client and transferring data to the client
     */
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
                    boolean registered=true;
                    try {
                        curUserDetails = new UserDetails(msg1.username, PasswordHash.createHash(msg1.password), socketChannel);
                    } catch (NoSuchAlgorithmException e) {
                        Log.i("NioServer.java","Password hash function not found");
                        registered=false;
                    } catch (InvalidKeySpecException e) {
                        Log.i("NioServer.java","Invalid Password, hash couldn't be created");
                        registered=false;
                    }
                    curUserInfo=curUserDetails;
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
                            Log.i("NioServer.java","Password hash function not found");
                            i=users.size();
                            break;
                        } catch (InvalidKeySpecException e) {
                            Log.i("NioServer.java","Invalid Password, hash couldn't be created");
                            i=users.size();
                            break;
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
                Log.i("NioServer.java","a socketchannel was closed unexpectedly");
                socketChannel=null;
            }

        }

        /**
         * function to notify online users of change in userlist and/or users' online/offline status
         */
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

        /**
         * handle requests from client and listen to port
         */
        public void run(){
            try {
                this.sleep(500,900);
            } catch (InterruptedException e) {
                Log.i("AcceptClient","Thread was woken up prematurely");
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

