package com.why.conn_lib.client;

import android.util.Log;

import com.why.conn_lib.utils.DefaultAddress;
import com.why.conn_lib.model.Frame;
import com.why.conn_lib.model.FrameType;
import com.why.conn_lib.utils.NumCovertUtils;
import com.why.conn_lib.model.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by wuhongyun on 17-8-31.
 */

public class Client extends Thread {

    public static final String TAG = "Client";
    private static final boolean DEBUG = false;
    private final int STATE_SOCKET_OPEN = 1;//socket打开
    private final int STATE_SOCKET_CLOSE = 1 << 1;//socket关闭
    private final int STATE_SOCKET_FAILED = 1 << 2;//连接失败
    //当前状态
    private int state = STATE_SOCKET_CLOSE;
    //socket
    private DatagramSocket socket;
    //数据包-分拆分之前
    private LinkedBlockingQueue<Packet> mBlockingQueue = new LinkedBlockingQueue<>();

    //默认单个数据包的数据容量
    private final int MAX_PACKET_SIZE = 1;
    /**
     * 互斥锁，不同时及进行读写
     */
    private Object mLock = new Object();
    //
    private InetAddress inetAddress;
    private int port;

    public Client(){

        try {
            socket = new DatagramSocket(null);
            state = STATE_SOCKET_OPEN;
        } catch (SocketException e) {
            e.printStackTrace();
            state = STATE_SOCKET_FAILED;
        }
    }

    /**
     * 设置发送的数据报的送达地址和端口号
     *
     * @param inetAddress 地址
     * @param port 端口
     */
    public void open(InetAddress inetAddress,int port){
        this.inetAddress = inetAddress;
        this.port = port;

    }


    @Override
    public void run() {
        if (DEBUG){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (inetAddress==null)
            inetAddress = DefaultAddress.ADDRESS;
        if (port==0)
            port = DefaultAddress.PORT;
            //不管有无数据此处都出于阻塞状态
            while (state == STATE_SOCKET_OPEN){

                Packet packet = null;
                //当有数据时
                while ((packet = pollData())!=null){
                    //进行数据发送,根据数据长度分包
                    byte[] data = packet.getPacket();

                    int dataLength = data.length;
                    //分包的数量
                    int multiPackSize = (dataLength%MAX_PACKET_SIZE==0) ? (dataLength/MAX_PACKET_SIZE):(dataLength/MAX_PACKET_SIZE+1);
                    //发送分包
                    for (int i = 0; i<multiPackSize;i++){
                        byte[] partData = null;
                        if (i==multiPackSize -1){//最后一包

                            partData = new byte[dataLength - i * MAX_PACKET_SIZE];
                        }else{
                            partData = new byte[MAX_PACKET_SIZE];
                        }
                        System.arraycopy(data,i * MAX_PACKET_SIZE,partData,0,partData.length);

                        //发送partData数据
                        byte[] frame = handleFrame(partData, i,multiPackSize,packet.getId());
                        DatagramPacket datagramPacket = new DatagramPacket(frame,frame.length,inetAddress,port);
                        try {
                            socket.send(datagramPacket);
                            if (onSendDataListener!=null){
                                onSendDataListener.onSendPartData(frame);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            //发送失败
                        }

                    }
                    if (onSendDataListener!=null){
                        onSendDataListener.onSendData(data);
                    }
                }
                if (DEBUG){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }

    }

    /**
     * 此方法用于发送数据帧，确定数据帧格式
     *
     * @param partData 当前帧需要发送的数据内容
     * @param frameSize 总帧数
     * @param frameSerial 当前帧序号
     */
    private byte[] handleFrame(byte[] partData, int frameSerial, int frameSize,int frameId) {
        //序列号转换


        Frame frame = new Frame(
                FrameType.TYPE_MUSIC,
                partData,
                NumCovertUtils.shortToBytes((short) frameSerial),
                NumCovertUtils.shortToBytes((short) frameSize),
                NumCovertUtils.intToBytes(frameId));

        byte[] bytes = frame.getFrame();

        Log.i(TAG,"frame:"+frame);
        return bytes;
    }

    /**
     * 发送数据,如果此方法循环调用，可能锁一直被此方法持有，导致pollData方法阻塞，数据无法发送
     *
     * @param data 要发送的数据
     * @return FRAME_ID packet数据的id
     */
    public int sendData(String data){

        Packet packet = new Packet();
        //装包
        packet.pack(data);
        synchronized (mLock){
            mBlockingQueue.add(packet);
        }


        Log.i(TAG,"mBlockingQueue.size:"+mBlockingQueue.size());
        return packet.getId();

    }

    /**
     * 取数据
     * @return packet数据
     */
    Packet pollData(){
        Packet packet = null;
        synchronized (mLock){
            packet = mBlockingQueue.poll();
        }
        return packet;
    }

    public void setOnSendDataListener(OnSendDataListener onSendDataListener) {
        this.onSendDataListener = onSendDataListener;
    }

    private OnSendDataListener onSendDataListener;

}
