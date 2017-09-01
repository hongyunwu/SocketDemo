package com.why.socketdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class ServerService extends Service {
    public ServerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket socket = new DatagramSocket(null);
                    socket.setReuseAddress(true);
                    socket.bind(new InetSocketAddress(Port.ADDRESS,Port.PORT));
                    byte[] buf = new byte[1024];
                    DatagramPacket inPacket = new DatagramPacket(buf,buf.length);
                    while (true){
                        Thread.sleep(2000);
                        socket.receive(inPacket);
                        Log.i("Server","receivedata:"+new String(inPacket.getData(),0,inPacket.getLength()));

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
