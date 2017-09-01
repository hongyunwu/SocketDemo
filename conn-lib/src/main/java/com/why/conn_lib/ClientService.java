package com.why.conn_lib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by wuhongyun on 17-8-31.
 *
 * socket的发送方：负责发送歌曲信息 图片等
 */

public class ClientService extends Service {
    private Client client0;
    private Client client1;


    /**
     * 协议设计：
     * 帧头：1～2
     * 长度：3～4
     * 类型：5～8
     * 总帧数：
     * 帧序号：
     * 数据段：
     * CRC校验：
     * 数据id：
     *
     *
     *
     *
     *
     *
     *
     * 帧尾：2字节
     */
    /*********************************************************/


    @Override
    public void onCreate() {
        super.onCreate();
        client0 = new Client();
        client0.start();
        client1 = new Client();
        client1.start();
        client1.sendData("我地门打算");
        client0.sendData("sadasadsa");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }


}
