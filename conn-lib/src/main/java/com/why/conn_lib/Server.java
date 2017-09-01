package com.why.conn_lib;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Created by wuhongyun on 17-9-1.
 */

public class Server extends Thread {

    public static final String TAG = "Server";
    private static final boolean DEBUG = true;

    private final int STATE_SOCKET_OPEN = 1;//socket打开
    private final int STATE_SOCKET_CLOSE = 1 << 1;//socket关闭
    private final int STATE_SOCKET_FAILED = 1 << 2;//连接失败
    private final int STATE_SOCKET_BIND = 1 << 3;//端口绑定
    //当前状态
    private int state = STATE_SOCKET_CLOSE;
    //socket
    private DatagramSocket socket;

    private InetAddress inetAddress;
    private int port;

    public Server(){
        try {
            socket = new DatagramSocket(null);
            state = STATE_SOCKET_OPEN;
        } catch (SocketException e) {
            e.printStackTrace();
            state = STATE_SOCKET_FAILED;
        }

    }

    /**
     * 绑定地址和端口
     * @param inetAddress
     * @param port
     */
    public void bind(InetAddress inetAddress,int port){
        this.inetAddress = inetAddress;
        this.port = port;

    }

    @Override
    public void run() {
        //进行端口绑定
        if (inetAddress == null){
            inetAddress = DefaultAddress.ADDRESS;

        }
        if (port==0){
            port = DefaultAddress.PORT;
        }
        try {
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(inetAddress,port));
            state = STATE_SOCKET_BIND;
        } catch (SocketException e) {
            e.printStackTrace();
            state = STATE_SOCKET_FAILED;
        }

        //不断接收
        while (state ==STATE_SOCKET_BIND){
            byte[] buf = new byte[Short.MAX_VALUE&0xffff];
            DatagramPacket inPacket = new DatagramPacket(buf,buf.length);
            while (true){
                try {
                    socket.receive(inPacket);
                    //
                    //解析
                    byte[] data = new byte[inPacket.getLength()];
                    System.arraycopy(inPacket.getData(),0,data,0,inPacket.getLength());
                    Log.i(TAG,"receive:"+ Arrays.toString(data));

                    handleFrame(data);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }


        }





    }

    /**
     * 接收到帧数据,需要进行组包
     *
     * @param data
     */
    private void handleFrame(byte[] data) {
        //重新构建一个frame出来，装到一个集合中



    }
}
