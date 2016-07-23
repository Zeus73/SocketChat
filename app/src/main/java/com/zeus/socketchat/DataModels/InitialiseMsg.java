package com.zeus.socketchat.dataModels;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Class to construct the Initial Message to be sent to the server to login/register the user
 * Created by Zeus on 6/21/2016.
 */
public class InitialiseMsg implements Serializable {
    private static final long serialVersionUID= 7421292146571360444L;
    public InitialiseMsg(String username,String password,boolean isNewUser){
        this.username=username;
        this.password=password;
        this.isNewUser=isNewUser;
    }

    public String username;
    public String password;
    public boolean isNewUser;

    public static byte[] serialize(InitialiseMsg msg){
        try{
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            ObjectOutputStream oos=new ObjectOutputStream(bos);
            oos.writeObject(msg);
            return bos.toByteArray();

        }catch(IOException e){
            Log.i("InitialiseMsg.java","serialisation error");
            return null;
        }

    }

    public static InitialiseMsg deserialize(byte[] byteArray){
        try{
            ByteArrayInputStream b=new ByteArrayInputStream(byteArray);
            ObjectInputStream o=new ObjectInputStream(b);
            return (InitialiseMsg) o.readObject();
        }catch(IOException e){
            Log.i("InitialiseMsg.java","Deserialisation error: IOexception");
            return null;
        } catch (ClassNotFoundException e) {
            Log.i("InitialiseMsg.java","Deserialisation error: ClassNotFoundException");
            return null;
        }

    }
}
