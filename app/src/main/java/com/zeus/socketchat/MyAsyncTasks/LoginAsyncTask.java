package com.zeus.socketchat.MyAsyncTasks;

import android.os.AsyncTask;

import com.zeus.socketchat.Client;

/**
 * The AsyncTask that performs the function of Client registration/Login with the server
 * Created by Zeus on 6/4/2016.
 */
public class LoginAsyncTask extends AsyncTask {


    LoginAsyncTaskInterface listener;
    public interface LoginAsyncTaskInterface{
        void onLoginAttempt(int authenticated);
    }

    /**
     * The function that return the Register/Login status to the activity that implements LoginAsyncTaskInterface
     * @param authenticated Integer denoting the register/login status
     */
    @Override
    protected void onPostExecute(Object authenticated) {
        listener.onLoginAttempt((int)authenticated);
    }

    /**
     * FUnction to initialise the listener object with the Context object of the calling activity
     * @param listener the context of activity implementing LoginAsyncTaskInterface
     */
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
