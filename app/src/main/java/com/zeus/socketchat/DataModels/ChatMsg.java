package com.zeus.socketchat.DataModels;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


/**
 * Class to construct the message details for a chat message to be sent from client to server
 */
@Table(name="ChatMsg")
public class ChatMsg extends Model implements Serializable {

    private static final long serialVersionUID= 3321292146571360444L;

    public ChatMsg(){
        super();
    }

    public ChatMsg(Date date, int msgType, String sender, String recipient, String msgContent,
                   ArrayList<OtherUsersInfo> usersList) {
        super();
        this.date = date;
        this.msgType = msgType;
        this.sender = sender;
        this.recipient = recipient;
        this.msgContent = msgContent;
        this.usersList = usersList;

    }

    @Column(name="date")
    public Date date;
    @Column(name="sender")
    public String sender;
    @Column(name="recipient")
    public String recipient;
    @Column(name="msgContent")
    public String msgContent;

    public int msgType;
    public ArrayList<OtherUsersInfo> usersList;



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
            Log.i("ChatMsg.java","serialisation error");
            return null;
        }
    }

    public static ChatMsg deserialize(byte[] byteArray){
        try{
            ByteArrayInputStream b=new ByteArrayInputStream(byteArray);
            ObjectInputStream o=new ObjectInputStream(b);
            return (ChatMsg) o.readObject();
        }catch(IOException e){
            Log.i("ChatMsg.java","Deserialisation error: IOexception");
            return null;
        } catch (ClassNotFoundException e) {
            Log.i("ChatMsg.java","Deserialisation error: ClassNotFoundException");
            return null;
        }

    }

}
