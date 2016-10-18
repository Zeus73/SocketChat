package com.zeus.socketchat;


import android.text.Selection;
import android.util.Log;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.zeus.socketchat.dataModels.ChatMsg;
import com.zeus.socketchat.dataModels.InitialiseMsg;
import com.zeus.socketchat.dataModels.OtherUsersInfo;
import com.zeus.socketchat.dataModels.PendingServerMsgs;
import com.zeus.socketchat.dataModels.UserDetails;

import java.nio.*;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


import java.io.IOException;
import java.net.*;

/**
 * The class that store the server details, list of registered users, list of ongoing threads
 */
public class NioServer {
    static public String serverIP;
    public static Vector<UserDetails> users;
    public static boolean toContinueServer=true;
    //ArrayList<AcceptClient> threadList;
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    public static HashMap<SocketChannel,AcceptClient> myMap;
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

        //threadList=new ArrayList<>();
        try {
            serverSocketChannel=ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(Client.PORT));
            serverSocketChannel.configureBlocking(false);
            serverIP=getIpAddress();
            Client.IP=serverIP;
            System.out.println("The Server is Online! at: "+ serverIP);
            selector=Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("The Server failed to start");
        }


    }

    private String getIpAddress() {
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
        myMap=new HashMap<>();
        try{
            //threadList.clear();
            AcceptClient newClient;
            while(toContinueServer){

                int toServe=selector.select();
                Iterator selectedKeys=selector.selectedKeys().iterator();
                while(selectedKeys.hasNext()){
                    SelectionKey key = (SelectionKey) selectedKeys.next();
                    selectedKeys.remove();
                    if(!key.isValid())
                        continue;

                    if(key.isAcceptable()){
                        SocketChannel socketChannel=serverSocketChannel.accept();
                        if(socketChannel!=null) {
                            System.out.println("New Client request!");
                            newClient = new AcceptClient(socketChannel);
                        }
                    }else if(key.isReadable()){
                        //perform read here
                        ByteBuffer buf=ByteBuffer.allocate(10240);
                        SocketChannel socketChannel= (SocketChannel) key.channel();
                        int shouldEnter=socketChannel.read(buf);
                        if(shouldEnter!=-1) {
                            ChatMsg chatMsg = ChatMsg.deserialize(buf.array());
                            buf.clear();
                            AcceptClient clientConcerned= myMap.get(socketChannel);
                            if(clientConcerned!=null){
                                MyServerWorker myServerWorker=new MyServerWorker(clientConcerned,chatMsg);
                                Thread thread2=new Thread(myServerWorker);
                                thread2.start();
                            }

                        }
                    }
                }

            }

            serverSocketChannel.close();
            serverIP=null;

        }catch(IOException e){
            serverSocketChannel=null;
            serverIP=null;
        }
    }


    /**
     * class extending thread to listen to a specified SocketChannel client and transferring data to the client
     */
    public class AcceptClient {

        SocketChannel socketChannel;
        UserDetails curUserInfo;

        private void authUserTasks(boolean para) throws IOException {
            ByteBuffer wrappingBuffer;
            wrappingBuffer=ByteBuffer.wrap(InitialiseMsg.serialize(new InitialiseMsg("true", null, para)));
            while(wrappingBuffer.hasRemaining())
                socketChannel.write(wrappingBuffer);
            this.refreshUsersList();
            //start();
            //what to do instead of start here?
            myMap.put(socketChannel,this);
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);
            WelcomeWorkerThread welcomeWorkerThread=new WelcomeWorkerThread(this);
            Thread thread1=new Thread(welcomeWorkerThread);
            thread1.start();
        }

        public AcceptClient(SocketChannel socketChannel){
            ByteBuffer buf,wrappingBuffer;
            this.socketChannel=socketChannel;
            buf=ByteBuffer.allocate(10240);
            try {
                int bytesRead=socketChannel.read(buf);
                InitialiseMsg msg1=InitialiseMsg.deserialize(buf.array());
                buf.clear();
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

                        authUserTasks(true);

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
                                users.get(i).socketChannel=socketChannel;

                                authUserTasks(false);

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
            ByteBuffer wrappingBuffer;
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


    }
}

