package com.zeus.socketchat.DataModels;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
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
            e.printStackTrace();
        }

        return null;
    }

    public static InitialiseMsg deserialize(byte[] byteArray){
        try{
            ByteArrayInputStream b=new ByteArrayInputStream(byteArray);
            ObjectInputStream o=new ObjectInputStream(b);
            return (InitialiseMsg) o.readObject();
        }catch(IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }
}
