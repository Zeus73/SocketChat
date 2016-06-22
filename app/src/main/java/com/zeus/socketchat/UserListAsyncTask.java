package com.zeus.socketchat;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Zeus on 6/4/2016.
 */
public class UserListAsyncTask extends AsyncTask {

    static UserListAsyncTaskInterface listener;
    public interface UserListAsyncTaskInterface{
        void onUserListFetch(ArrayList<String> userList);
    }

    @Override
    protected void onPostExecute(Object o) {
        listener.onUserListFetch((ArrayList)o);
    }
    static public void setUserListAsyncTaskListener(UserListAsyncTaskInterface listener){
        UserListAsyncTask.listener=listener;
    }

    @Override
    protected Object doInBackground(Object[] params) {

        ArrayList<String> usersList=Client.getUsersList();
        return usersList;
    }
}
