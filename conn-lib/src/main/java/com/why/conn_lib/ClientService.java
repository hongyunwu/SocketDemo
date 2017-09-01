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
    private Client client;


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
        client = new Client();
        client.start();
        for (int i = 0;i<2;i++){
            client.sendData("1312314252334342");
        }

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }


}
