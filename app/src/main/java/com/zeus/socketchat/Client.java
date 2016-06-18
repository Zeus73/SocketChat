package com.zeus.socketchat;

import android.util.Log;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;


/**
 * Created by Zeus on 6/4/2016.
 */
public class Client implements  Serializable {
    static Socket clientSoc;
    static DataOutputStream dout;
    static DataInputStream din;
    static final int PORT = 7777;
//    static final String IP = "10.0.2.2";
    static String IP = "192.168.173.1";
    static String sender;

    static public int initClient(String curUsername, String password, boolean newUser) {
        sender = curUsername;
        try {
            clientSoc = new Socket(IP, PORT);

            dout = new DataOutputStream(clientSoc.getOutputStream());
            din = new DataInputStream(clientSoc.getInputStream());
            dout.writeUTF(sender);
            dout.writeUTF(password);
            dout.writeBoolean(newUser);
            int result = din.readInt();
            return result;

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 1;
    }

    static public ArrayList<String> getUsersList(){
        ArrayList<String> ret=new ArrayList<>();

        try{
            dout.writeUTF(sender+" listUsers");
            String txt=din.readUTF();
            StringTokenizer stringTokenizer=new StringTokenizer(txt);

            while(stringTokenizer.hasMoreTokens()){
                String temp=stringTokenizer.nextToken();
                if(!temp.equals(sender))
                    ret.add(temp);
            }
        }catch(Exception ex){

        }
        return ret;
    }


}

