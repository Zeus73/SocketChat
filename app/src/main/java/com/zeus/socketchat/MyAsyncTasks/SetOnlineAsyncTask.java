package com.zeus.socketchat.MyAsyncTasks;

import android.os.AsyncTask;

import com.zeus.socketchat.Client;

/**
 * Created by Zeus on 6/30/2016.
 */
public class SetOnlineAsyncTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] params) {
        String username= (String) params[0];
        String password= (String) params[1];
        boolean newUser= (boolean) params[2];
        Client.initClient(username,password,newUser);
        return null;
    }
}
