package com.zeus.socketchat.DataModels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;
import java.nio.channels.SocketChannel;

/**
 * Created by Zeus on 6/30/2016.
 */
@Table(name="UserDetails")
public class UserDetails extends Model implements Serializable {
    @Column(name="username")
    public String username;
    @Column(name="password")
    public String password;

    public SocketChannel socketChannel;

    public UserDetails(){super();}

    public UserDetails(String clientLoginName, String clientPassword, SocketChannel socketChannel) {
        super();
        this.username=clientLoginName;
        this.password=clientPassword;
        this.socketChannel=socketChannel;
    }

    public boolean isUserOnline(){
        return (socketChannel!=null&&socketChannel.isOpen()&&socketChannel.isConnected());
    }
}