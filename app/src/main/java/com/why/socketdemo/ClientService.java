package com.why.socketdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;

import static com.why.socketdemo.Port.ADDRESS;


public class ClientService extends Service {
    public ClientService() {
    }
    static int PORT = 1111;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i("client","ADDRESS:"+ ADDRESS);
                    DatagramSocket socket = new DatagramSocket(null);

                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0;i <100;i++){
                        stringBuilder.append("send msg from android-monkey");
                    }
                    byte[] msg = stringBuilder.toString().getBytes();
                    while (true){
                        Thread.sleep(2000);

                        DatagramPacket datagramPacket = new DatagramPacket(msg,msg.length,Port.ADDRESS,Port.PORT);
                        socket.send(datagramPacket);
                        Log.i("client","senddata->"+new String(datagramPacket.getData()));

                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        }).start();


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(Port.ADDRESS,Port.PORT);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
