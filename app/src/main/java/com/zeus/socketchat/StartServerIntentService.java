package com.zeus.socketchat;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class StartServerIntentService extends IntentService {


    public StartServerIntentService() {
        super("StartServerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            NioServer nioServer=new NioServer();
//        serverStartedListener.onServerStarted(nioServer);
            nioServer.startNioServer();
        }
    }
}
