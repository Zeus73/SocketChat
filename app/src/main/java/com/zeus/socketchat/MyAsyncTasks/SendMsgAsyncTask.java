package com.zeus.socketchat.MyAsyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.zeus.socketchat.Client;
import com.zeus.socketchat.DataModels.ChatMsg;

import java.io.IOException;

/**
 * The AsyncTask that performs the function of sending messages to the server and/or performing logout
 * Created by Zeus on 6/7/2016.
 */
public class SendMsgAsyncTask extends AsyncTask {

    /**
     * Performs Client logout and closes the socketChannel if the msgType was ChatMsg.LOGOUT
     * @param o denotes whether the client requested to be logged out
     */
    @Override
    protected void onPostExecute(Object o) {
        boolean needToLogout= (boolean) o;
        if(needToLogout)
            try {
                Client.clientSocketChannel.close();
            } catch (IOException e) {
                Log.i("SendMsgAsyncTask","The user is already logged out");
            }
        super.onPostExecute(o);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        ChatMsg analyseMsg=(ChatMsg)params[0];

        Client.sendChatMsg(analyseMsg);
        if(analyseMsg.msgType==ChatMsg.LOGOUT)
            return true;
        return false;
    }
}
