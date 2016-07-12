package com.zeus.socketchat.DataModels;

import android.net.wifi.WifiConfiguration;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by Zeus on 7/12/2016.
 */
@Table(name="MyWifiConfig")
public class MyWifiConfig  extends Model implements Serializable{
    @Column(name="wifiConfig")
    public WifiConfiguration wifiConfig;

    public MyWifiConfig(){super();}
    public MyWifiConfig(WifiConfiguration wfc){
        super();
        wifiConfig=wfc;
    }
}
