package com.zeus.socketchat.DataModels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Zeus on 7/2/2016.
 */
@Table(name="PendingServerMsgs")
public class PendingServerMsgs extends Model implements Serializable {

    public PendingServerMsgs(){
        super();
    }

    public PendingServerMsgs(ChatMsg msg1) {
        super();
        this.date = msg1.date;
        this.msgType = msg1.msgType;
        this.sender = msg1.sender;
        this.recipient = msg1.recipient;
        this.msgContent = msg1.msgContent;
        this.usersList = msg1.usersList;
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

    public ChatMsg getChatMsg(){
        ChatMsg msg1=new ChatMsg(date,msgType,sender,recipient,msgContent,usersList);
        return msg1;
    }
}
