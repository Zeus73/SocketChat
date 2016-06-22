package com.zeus.socketchat;

import android.system.Os;
import android.util.Log;

import java.net.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

/**
 * Created by Zeus on 6/4/2016.
 */
public class Client implements  Serializable {
    static SocketChannel clientSocketChannel;
    static ByteBuffer buf;
    static ByteBuffer wrappingBuffer;
    static final int PORT = 7777;
//    static String IP = "10.0.2.2";
    static String IP = "192.168.0.104";
    static String sender;
    static String currentlyChattingWith=null;

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
            if(yy.username.equals("true"))
                return 0;
//            Log.i("return value",String.valueOf(yy));
//            return yy;
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

    static public ArrayList<String> getUsersList(){
        ArrayList<String> ret=new ArrayList<>();

        try{

            wrappingBuffer=ByteBuffer.wrap(ChatMsg.serialize(new ChatMsg(null,ChatMsg.LIST_USERS,sender,null,null,null)));
            while(wrappingBuffer.hasRemaining()){
                clientSocketChannel.write(wrappingBuffer);
            }

            buf.clear();
            clientSocketChannel.read(buf);
            ChatMsg retMsg=ChatMsg.deserialize(buf.array());
            ret=retMsg.usersList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
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

