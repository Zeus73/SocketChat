package com.zeus.socketchat;

import com.activeandroid.query.Select;
import com.zeus.socketchat.DataModels.ChatMsg;
import com.zeus.socketchat.DataModels.InitialiseMsg;
import com.zeus.socketchat.DataModels.OtherUsersInfo;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zeus on 6/4/2016.
 */
public class Client implements  Serializable {
    public static SocketChannel clientSocketChannel;
    public static ByteBuffer buf;
    public static ByteBuffer wrappingBuffer;
    public static final int PORT = 7777;
    public static String IP = "192.168.43.1";
//    static String IP = "192.168.0.103";
//    static String IP = "10.0.2.15";
//    static String IP = "127.0.0.1";


    public static ArrayList<OtherUsersInfo> friendsList=new ArrayList<>();
    public static String sender;
    public static String currentlyChattingWith=null;

    static public int initClient(String curUsername, String password, boolean newUser) {
        sender = curUsername;

        try {
            clientSocketChannel = SocketChannel.open();
            clientSocketChannel.connect(new InetSocketAddress(IP,PORT));

            buf=ByteBuffer.allocate(10240);
            InitialiseMsg regMsg=new InitialiseMsg(curUsername,password,newUser);

            wrappingBuffer=ByteBuffer.wrap(InitialiseMsg.serialize(regMsg));
            while(wrappingBuffer.hasRemaining()){
                clientSocketChannel.write(wrappingBuffer);
            }

            buf.clear();
            clientSocketChannel.read(buf);
            InitialiseMsg yy=InitialiseMsg.deserialize(buf.array());
            if(yy.username.equals("true")){
                friendsList.clear();
                List<OtherUsersInfo> databaseFriendList=  new Select().
                        from(OtherUsersInfo.class).execute();
                if(databaseFriendList!=null)
                    for(int i=0;i<databaseFriendList.size();++i)
                        friendsList.add(databaseFriendList.get(i));
                return 0;
            }

            else if(yy.isNewUser){
                clientSocketChannel.close();
                return 2;
            }
            clientSocketChannel.close();
            return 1;


        } catch (IOException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
        }

        return 1;
    }

    static public void populateFriendListFromDatabase(){
        friendsList.clear();
        List<OtherUsersInfo> databaseFriendList=  new Select().
                from(OtherUsersInfo.class).execute();
        if(databaseFriendList!=null)
            for(int i=0;i<databaseFriendList.size();++i)
                friendsList.add(databaseFriendList.get(i));
    }

    public static void sendChatMsg(ChatMsg msg1){
        wrappingBuffer=ByteBuffer.wrap(ChatMsg.serialize(msg1));

        try {
                if(clientSocketChannel.isOpen())
                    while(wrappingBuffer.hasRemaining())
                        clientSocketChannel.write(wrappingBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

