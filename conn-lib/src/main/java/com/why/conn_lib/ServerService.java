package com.why.conn_lib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by wuhongyun on 17-9-1.
 */

public class ServerService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        Server server = new Server();
        server.start();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }
}
