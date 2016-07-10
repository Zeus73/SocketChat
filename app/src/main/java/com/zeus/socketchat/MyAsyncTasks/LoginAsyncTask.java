package com.zeus.socketchat.MyAsyncTasks;

import android.os.AsyncTask;

import com.zeus.socketchat.Client;

/**
 * Created by Zeus on 6/4/2016.
 */
public class LoginAsyncTask extends AsyncTask {


    LoginAsyncTaskInterface listener;
    public interface LoginAsyncTaskInterface{
        void onLoginAttempt(int authenticated);
    }

    @Override
    protected void onPostExecute(Object authenticated) {
        listener.onLoginAttempt((int)authenticated);
    }

    public void SetLoginAsynctaskListener(LoginAsyncTaskInterface listener){
    this.listener=listener;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String username= (String) params[0];
        String password= (String) params[1];
        boolean newUser= (boolean) params[2];
        int authenticated= Client.initClient(username,password,newUser);

        return authenticated;
    }
}
