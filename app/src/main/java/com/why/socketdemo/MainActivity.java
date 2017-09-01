package com.why.socketdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Intent client;
    private Intent server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        server = new Intent(this, com.why.conn_lib.ServerService.class);
        startService(server);
        client = new Intent(this, com.why.conn_lib.ClientService.class);
        startService(client);

        byte[] bytes = "dasdasdsadass".getBytes();

        Log.i(TAG,"byte:"+Integer.toString(((byte) 0xFF) &0xff));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(client);
        stopService(server);
    }
}
