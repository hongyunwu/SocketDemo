package com.why.conn_lib;


import java.net.DatagramPacket;

public class Packet{
	
	private int id=AtomicIntegerUtil.getIncrementID();
	private byte[] data;

	public int getId() {
		return id;
	}

	public void pack(String txt)
	{
		data=txt.getBytes();
	}
	
	public void pack(byte[] temp)
	{
		data = temp;
	}

	public byte[] getPacket()
	{
		return data;
	}


	/**
	 * 如果包里边有数据的话，那么就tostring数据，如果没有就打印地址
	 * @return
	 */
	@Override
	public String toString() {


		return (data!=null&&data.length>0) ? new String(data):super.toString();
	}
}