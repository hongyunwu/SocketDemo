package com.why.conn_lib;

/**
     * Socket收到数据监听
     */
public interface OnReceiveDataListener{
    /**
     * 收到完整数据
     * @param data
     */
    void onReceiveData(byte[] data);

    /**
     * 收到部分数据
     * @param data
     */
    void onReceivePartData(byte[] data);
}