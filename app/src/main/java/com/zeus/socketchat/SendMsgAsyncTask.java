package com.zeus.socketchat;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

/**
 * Created by Zeus on 6/7/2016.
 */
public class SendMsgAsyncTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] params) {
        String msgToBeSent=(String)params[0];
        try{
            Client.dout.writeUTF(msgToBeSent);
            Log.i("Sending Msg",msgToBeSent);
        }catch (IOException e){
            Log.i("Exception","User is Logged Out");
        }
        return null;
    }
}
