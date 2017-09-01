package com.why.conn_lib.server;

import android.util.Log;

import com.why.conn_lib.utils.DefaultAddress;
import com.why.conn_lib.utils.NumCovertUtils;
import com.why.conn_lib.model.Frame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;

/**
 * Created by wuhongyun on 17-9-1.
 */

public class Server extends Thread {

    public static final String TAG = "Server";
    private static final boolean DEBUG = true;

    /**
     * 缓冲池里边最多同时放1000个帧
     */
    private static final int LRU_FRAME_SIZE = 1000;

    private final int STATE_SOCKET_OPEN = 1;//socket打开
    private final int STATE_SOCKET_CLOSE = 1 << 1;//socket关闭
    private final int STATE_SOCKET_FAILED = 1 << 2;//连接失败
    private final int STATE_SOCKET_BIND = 1 << 3;//端口绑定

    //超时检查器
    private final TimeOutChecker timeOutChecker;
    private final Object mLock = new Object();
    //当前状态
    private int state = STATE_SOCKET_CLOSE;
    //socket
    private DatagramSocket socket;

    private InetAddress inetAddress;
    private int port;

    //算法 - LRU算法
    LinkedHashMap<Integer,ArrayList<Frame>> frames;

    //比较器
    private Comparator<? super Frame> frameComparator = new Comparator<Frame>() {
        @Override
        public int compare(Frame frame1, Frame frame2) {
            return new Integer(frame1.getFrameSerial()).compareTo(new Integer(frame2.getFrameSerial()));
        }
    };

    public Server(){
        try {
            socket = new DatagramSocket(null);
            state = STATE_SOCKET_OPEN;
        } catch (SocketException e) {
            e.printStackTrace();
            state = STATE_SOCKET_FAILED;
        }

        frames = new LinkedHashMap<>();
        timeOutChecker = new TimeOutChecker(mLock,frames);

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
            timeOutChecker.check();//开始超时检查
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
                    //解析
                    byte[] data = new byte[inPacket.getLength()];
                    System.arraycopy(inPacket.getData(),0,data,0,inPacket.getLength());
                    handleFrame(data);
                    if (onReceiveDataListener!=null){
                        onReceiveDataListener.onReceivePartData(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }


        }
        //退出超时检查
        timeOutChecker.quit();




    }

    /**
     * 接收到帧数据,需要进行组包
     *
     * @param data
     */
    private void handleFrame(byte[] data) {
        //重新构建一个frame出来，装到一个集合中
        Frame frame = new Frame(data);
        Log.i(TAG,"frame:"+frame+",id->"+frame.getFrameId());
        timeOutChecker.update(frame.getFrameId());//更新frameId时间
        //根据帧id，及当前帧的序列号
        ArrayList<Frame> frames = this.frames.get(frame.getFrameId());
        if (frames==null){
            frames = new ArrayList<>();
            frames.add(frame);
            if (frames.size()==frame.getFrameSize()){
                //TODO 数据帧接收完毕
                //组合数据
                combineFrame(frames);
                synchronized (mLock){
                    this.frames.remove(frame.getFrameId());
                }

            }else{
                synchronized (mLock){
                    this.frames.put(frame.getFrameId(),frames);
                }
            }

        }else{
            frames.add(frame);

            if (frames.size()==frame.getFrameSize()){
                //TODO 数据帧接收完毕
                //组合数据
                combineFrame(frames);
                synchronized (mLock){
                    this.frames.remove(frame.getFrameId());
                }
            }else{
                synchronized (mLock){
                    this.frames.put(frame.getFrameId(),frames);
                }

            }
        }



    }


    /**
     * 当前数据包接收完成，进行数据组合
     * @param frames
     */
    private void combineFrame(ArrayList<Frame> frames) {
        Log.i(TAG,"开始组合数据："+frames);
        //排序
        Collections.sort(frames,frameComparator);
        //取出data
        byte[] bytes = new byte[0];
        for (Frame frame : frames){
            bytes = NumCovertUtils.combineBytes(bytes,frame.getData());
        }
        Log.i(TAG,"combineFrame:"+new String(bytes));
        if (onReceiveDataListener!=null){
            onReceiveDataListener.onReceiveData(bytes);
        }

    }

    public void setOnReceiveDataListener(OnReceiveDataListener onReceiveDataListener) {
        this.onReceiveDataListener = onReceiveDataListener;
    }

    private OnReceiveDataListener onReceiveDataListener;


}
