package com.zeus.socketchat.MyAsyncTasks;

import android.os.AsyncTask;

import com.zeus.socketchat.Client;
import com.zeus.socketchat.DataModels.ChatMsg;

import java.io.IOException;

/**
 * Created by Zeus on 6/7/2016.
 */
public class SendMsgAsyncTask extends AsyncTask {

    @Override
    protected void onPostExecute(Object o) {
        boolean needToLogout= (boolean) o;
        if(needToLogout)
            try {
                Client.clientSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        super.onPostExecute(o);
    }

    @Override
    protected Object doInBackground(Object[] params) {
//        Log.i("Sending Msg",msgToBeSent);
        ChatMsg analyseMsg=(ChatMsg)params[0];
//        Log.i("hulu",String.valueOf(analyseMsg.msgType));
        Client.sendChatMsg(analyseMsg);
        if(analyseMsg.msgType==ChatMsg.LOGOUT)
            return true;
        return false;
    }
}
