package com.why.conn_lib.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class AtomicIntegerUtil {

	private static final AtomicInteger mAtomicInteger=new AtomicInteger();
	
	public static int  getIncrementID()
	{
		return mAtomicInteger.getAndIncrement();
	}
}