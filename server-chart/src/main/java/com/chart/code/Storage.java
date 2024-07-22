package com.chart.code;


import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储
 *
 * @author CleanCode
 */
public class Storage {
    public static Map<Integer, SocketChannel> userSocketsMap = new ConcurrentHashMap<>(256);
}
