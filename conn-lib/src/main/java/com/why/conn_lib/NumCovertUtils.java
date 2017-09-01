package com.why.conn_lib;

/**
 * Created by wuhongyun on 17-9-1.
 *
 * 进制转换工具
 */

public class NumCovertUtils {

    /**
     * 短整型转换为byte数组 - 长度2
     * @param data
     * @return
     */
    public static byte[] shortToBytes(short data) {

        return new byte[]{(byte) (data>>8&0xff),(byte) (data&0xff)};
    }

    /**
     * 整型转换为byte数组 - 长度4
     * @param data
     * @return
     */
    public static byte[] intToBytes(int data) {
        return new byte[]{(byte) (data>>24&0xff), (byte) (data>>16&0xff), (byte) (data>>8&0xff), (byte) (data&0xff)};
    }

    /**
     * byte数组 - 长度 4 转换为整型
     * @param data
     * @return
     */
    public static int bytesToInt(byte[] data) {

        return (data[3] & 0xff) | ((data[2] << 8) & 0xff00) // | 表示安位或
                | ((data[1] << 24) >>> 8) | (data[0] << 24);
    }

    /**
     * byte数组 - 长度2 转换为短整型
     * @param data
     * @return
     */
    public static short bytesToShort(byte[] data) {
        return (short) (((data[1]&0xff))|(data[0]<<8&0xff00));
    }

    /**
     *  byte数组组合
     * @param data0
     * @param data1
     * @return
     */
    public static byte[] combineBytes(byte[] data0, byte[] data1) {
        byte[] bytes = new byte[data0.length + data1.length];
        System.arraycopy(data0,0,bytes,0,data0.length);
        System.arraycopy(data1,0,bytes,data0.length,data1.length);
        return bytes;
    }
}
