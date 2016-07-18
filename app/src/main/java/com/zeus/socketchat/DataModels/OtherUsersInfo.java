package com.zeus.socketchat.DataModels;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Class to store the Client side list of friends registered on server
 * Created by Zeus on 6/30/2016.
 */
@Table(name="OtherUsersInfo")
public class OtherUsersInfo extends Model implements Serializable {

    private static final long serialVersionUID= 5521292146571360444L;
    @Column(name="friendUsername")
    public String friendUsername;
    @Column(name="isOnline")
    public boolean isOnline;

    public OtherUsersInfo(){super();}

    public OtherUsersInfo(String str1,boolean isOnline){
        super();
        this.friendUsername=str1;
        this.isOnline=isOnline;
    }
}
