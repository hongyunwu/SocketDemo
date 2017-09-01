package com.why.conn_lib;

import java.util.Arrays;

/**
 * Created by wuhongyun on 17-9-1.
 *
 * 数据帧
 *
 */

public class Frame {

    /**
     * 帧头
     */
    public static final byte[] FRAME_HEAD = {0x11,0x22};
    /**
     * 帧尾
     */
    public static byte[] FRAME_FOOTER = {0x22,0x11};

    /**
     * 帧id，用于标识帧是否属于同一个数据包
     */
    public byte[] FRAME_ID = new byte[4];
    /**
     * 帧长度2
     */
    public byte[] FRAME_LENGTH = new byte[2];

    /**
     * 帧类型
     */
    public byte[] FRAME_TYPE = new byte[4];

    /**
     * 数据
     */
    public byte[] data;

    /**
     * CRC校验码
     */

    public byte[] CRC = new byte[2];

    /**
     * 帧序号
     */
    public byte[] FRAME_SERIAL = new byte[2];

    /**
     * 帧数
     */
    public byte[] FRAME_SIZE = new byte[2];

    /**
     * 构造方法
     * @param FRAME_TYPE 数据类型
     * @param data  数据
     * @param FRAME_SERIAL 数据帧序号
     * @param FRAME_SIZE 数据帧总数
     */
    public Frame(byte[] FRAME_TYPE, byte[] data,  byte[] FRAME_SERIAL, byte[] FRAME_SIZE,byte[] FRAME_ID) {
        this.FRAME_TYPE = FRAME_TYPE;
        this.data = data;
        this.FRAME_SERIAL = FRAME_SERIAL;
        this.FRAME_SIZE = FRAME_SIZE;
        this.FRAME_ID = FRAME_ID;
    }


    public byte[] getFrameLength() {
        //进行组合       帧头 + 类型 + 帧序号 + 总帧数 + 帧长度 +             数据              + CRC + 帧尾
        int totalSize = FRAME_HEAD.length   +   FRAME_TYPE.length  +  FRAME_SERIAL.length   +   FRAME_SIZE.length   +  FRAME_LENGTH.length   +  (data!=null?data.length:0)   + CRC.length   +  FRAME_FOOTER.length;
        //计算帧长度
        FRAME_LENGTH[1] = (byte) (totalSize&0xff);
        FRAME_LENGTH[0] = (byte) (totalSize>>8&0xff);
        return FRAME_LENGTH;
    }

    public void setFrameLength(byte[] FRAME_LENGTH) {
        this.FRAME_LENGTH = FRAME_LENGTH;
    }

    public byte[] getFrameType() {
        return FRAME_TYPE;
    }

    public void setFrameType(byte[] FRAME_TYPE) {
        this.FRAME_TYPE = FRAME_TYPE;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getCRC() {
        return CRC;
    }

    public void setCRC(byte[] CRC) {
        this.CRC = CRC;
    }

    public byte[] getFrameSerial() {
        return FRAME_SERIAL;
    }

    public void setFrameSerial(byte[] FRAME_SERIAL) {
        this.FRAME_SERIAL = FRAME_SERIAL;
    }

    public byte[] getFrameSize() {
        return FRAME_SIZE;
    }

    public void setFrameSize(byte[] FRAME_SIZE) {
        this.FRAME_SIZE = FRAME_SIZE;
    }



    @Override
    public String toString() {
        return "Frame{" +
                "FRAME_ID=" + Arrays.toString(FRAME_ID) +
                ", FRAME_LENGTH=" + Arrays.toString(FRAME_LENGTH) +
                ", FRAME_TYPE=" + Arrays.toString(FRAME_TYPE) +
                ", data=" + new String(data) +
                ", CRC=" + Arrays.toString(CRC) +
                ", FRAME_SERIAL=" + Arrays.toString(FRAME_SERIAL) +
                ", FRAME_SIZE=" + Arrays.toString(FRAME_SIZE) +
                '}';
    }

    public byte[] getFrame(){
        //进行组合       帧头 + 类型 + 帧id + 帧序号 + 总帧数 + 帧长度 +             数据              + CRC + 帧尾
        int totalSize = FRAME_HEAD.length   +   FRAME_TYPE.length  +FRAME_ID.length+  FRAME_SERIAL.length   +   FRAME_SIZE.length   +  FRAME_LENGTH.length   +  (data!=null?data.length:0)   + CRC.length   +  FRAME_FOOTER.length;
        //计算帧长度
        FRAME_LENGTH[1] = (byte) (totalSize&0xff);
        FRAME_LENGTH[0] = (byte) (totalSize>>8&0xff);
        byte[] frame = new byte[totalSize];
        //帧头
        System.arraycopy(FRAME_HEAD,0,frame,0,FRAME_HEAD.length);
        //类型
        System.arraycopy(FRAME_TYPE,0,frame,FRAME_HEAD.length,FRAME_TYPE.length);
        //数据包id
        System.arraycopy(FRAME_ID,0,frame,FRAME_HEAD.length+FRAME_TYPE.length,FRAME_ID.length);
        //帧序号
        System.arraycopy(FRAME_SERIAL,0,frame,FRAME_HEAD.length+FRAME_TYPE.length+FRAME_ID.length,FRAME_SERIAL.length);
        //总帧数
        System.arraycopy(FRAME_SIZE,0,frame,FRAME_HEAD.length+FRAME_TYPE.length+FRAME_ID.length+FRAME_SERIAL.length,FRAME_SIZE.length);
        //帧长度
        System.arraycopy(FRAME_LENGTH,0,frame,FRAME_HEAD.length+FRAME_TYPE.length+FRAME_ID.length+FRAME_SERIAL.length+FRAME_SIZE.length,FRAME_LENGTH.length);
        //数据
        System.arraycopy(data,0,frame,FRAME_HEAD.length+FRAME_TYPE.length+FRAME_ID.length+FRAME_SERIAL.length+FRAME_SIZE.length+FRAME_LENGTH.length,data.length);
        //crc
        System.arraycopy(CRC,0,frame,FRAME_HEAD.length+FRAME_TYPE.length+FRAME_ID.length+FRAME_SERIAL.length+FRAME_SIZE.length+FRAME_LENGTH.length+data.length,CRC.length);
        //帧尾
        System.arraycopy(FRAME_FOOTER,0,frame,FRAME_HEAD.length+FRAME_TYPE.length+FRAME_ID.length+FRAME_SERIAL.length+FRAME_SIZE.length+FRAME_LENGTH.length+data.length+CRC.length,FRAME_FOOTER.length);
        return frame;
    }
}
