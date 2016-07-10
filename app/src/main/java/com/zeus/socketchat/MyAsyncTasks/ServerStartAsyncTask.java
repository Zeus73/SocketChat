package com.zeus.socketchat.MyAsyncTasks;

import android.os.AsyncTask;
import android.view.View;

import com.zeus.socketchat.NioServer;

/**
 * Created by Zeus on 6/23/2016.
 */
public class ServerStartAsyncTask extends AsyncTask {

//    public void setServerStartedListener(ServerStartInterface listener){
//        this.serverStartedListener=listener;
//    }
//
//    public interface ServerStartInterface{
//        public void onServerStarted(NioServer ns);
//    }
//
//    ServerStartInterface serverStartedListener;



    @Override
    protected Object doInBackground(Object[] params) {
        NioServer nioServer=new NioServer();
//        serverStartedListener.onServerStarted(nioServer);
        nioServer.startNioServer();
        return null;
    }
}
