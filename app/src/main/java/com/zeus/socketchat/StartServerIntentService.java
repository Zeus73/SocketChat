package com.zeus.socketchat;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * Service to start the server on the user's device
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class StartServerIntentService extends IntentService {


    public StartServerIntentService() {
        super("StartServerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            NioServer nioServer=new NioServer();
            nioServer.startNioServer();
        }
    }
}
