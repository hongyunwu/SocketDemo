package com.why.conn_lib;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by wuhongyun on 17-9-1.
 */

public class DefaultAddress {
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
