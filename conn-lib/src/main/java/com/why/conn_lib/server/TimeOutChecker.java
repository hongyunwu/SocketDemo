package com.why.conn_lib.server;

import android.os.SystemClock;

import com.why.conn_lib.model.Frame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * Created by wuhongyun on 17-9-1.
 *
 * 用于检查Server集合中的数据包，是否超时，超过时间则丢弃partData
 */

public class TimeOutChecker extends Thread{

    //60s检查一次
    private static final long CHECK_INTERVAL = 60*1000;

    private static final long TIME_OUT = 10*1000;
    private LinkedHashMap<Integer, ArrayList<Frame>> mFrames;
    private Object mLock;
    private boolean quit_flag;

    public TimeOutChecker(Object lock, LinkedHashMap<Integer, ArrayList<Frame>> frames){

        this.mLock = lock;
        this.mFrames = frames;

    }
    public LinkedHashMap<Integer,Long> checker = new LinkedHashMap<>();


    /**
     * 如果当前frame有数据来，那么就更新数据时间
     * @param frameId
     */
    public void update(int frameId){

        checker.put(frameId, SystemClock.uptimeMillis());
    }

    /**
     * 根据frameId 获取checker集合中的时间
     * @param frameId
     * @return time
     */
    public long getTime(int frameId){
        Long value = checker.get(frameId);

        if (value!=null){
            return value;
        }
        //当前集合中没有frameid的 time
        return 0;
    }

    /**
     * 检查是否有超时的frameId
     */
    public void check() {
        quit_flag = true;
        start();
    }

    /**
     * 退出
     */
    public void quit(){
        quit_flag = false;
    }

    @Override
    public void run() {
        ArrayList<Integer> timeOutFrameIds = new ArrayList<>();
        while (quit_flag){
            timeOutFrameIds.clear();
            Set<Integer> keySet = mFrames.keySet();

            Iterator<Integer> iterator = keySet.iterator();

            while (iterator.hasNext()){

                Integer frameId = iterator.next();
                Long updateTime = checker.get(frameId);
                if (updateTime!=null){
                    //与当前时间比较
                    if ((SystemClock.uptimeMillis() - updateTime)>TIME_OUT){
                        timeOutFrameIds.add(frameId);
                    }
                }
            }
            //移除frameId
            synchronized (mLock){
                for (int frameId : timeOutFrameIds){
                    mFrames.remove(frameId);
                }
            }
            try {
                Thread.sleep(CHECK_INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
