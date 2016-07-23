package com.zeus.socketchat.dataModels;

import android.net.wifi.WifiConfiguration;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * class to store the original Wifi Configuration of the user before modifying them
 * Created by Zeus on 7/12/2016.
 */
@Table(name="MyWifiConfig")
public class MyWifiConfig  extends Model implements Serializable{
    @Column(name="wifiConfig")
    public WifiConfiguration wifiConfig;
    @Column(name="isWifiOn")
    public boolean isWifiOn;
    public MyWifiConfig(){super();}
    public MyWifiConfig(WifiConfiguration wfc,boolean isWifiOn){
        super();
        this.isWifiOn=isWifiOn;
        wifiConfig=wfc;
    }
}
