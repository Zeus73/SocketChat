package com.zeus.socketchat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class ChatMsg implements Serializable {

    private static final long serialVersionUID= 3321292146571360444L;

    public ChatMsg(Date date, int msgType, String sender, String recipient, String msgContent,
                   ArrayList<String> usersList) {

        this.date = date;
        this.msgType = msgType;
        Sender = sender;
        this.recipient = recipient;
        this.msgContent = msgContent;
        this.usersList = usersList;
    }

    public Date date;
    public int msgType;
    public String Sender;
    public String recipient;
    public String msgContent;
    public ArrayList<String> usersList;


    public static final int CHAT=0;
    public static final int LIST_USERS=1;
    public static final int LOGOUT=2;

    public static final int NO_SUCH_USER=-1;
    public static final int USER_OFFLINE=-2;


    public static byte[] serialize(ChatMsg msg){
        try{
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(bos);
            oos.writeObject(msg);
            return bos.toByteArray();

        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public static ChatMsg deserialize(byte[] byteArray){
        try{
            ByteArrayInputStream b=new ByteArrayInputStream(byteArray);
            ObjectInputStream o=new ObjectInputStream(b);
            return (ChatMsg) o.readObject();
        }catch(IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

}
