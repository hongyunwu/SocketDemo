package com.why.conn_lib;

/**
 * Created by wuhongyun on 17-9-1.
 * Socket发送数据监听
 */

public interface OnSendDataListener {

    /**
     * 发送完整数据
     * @param data
     */
    void onSendData(byte[] data);

    /**
     * 发送部分数据
     * @param data
     */
    void onSendPartData(byte[] data);
}
