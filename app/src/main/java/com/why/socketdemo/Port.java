package com.why.socketdemo;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by wuhongyun on 17-8-31.
 */

public class Port {
    public static InetAddress ADDRESS;
    public static int PORT = 1111;

    static {

        try {
            ADDRESS = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }

}
