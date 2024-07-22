package com.chart.code;


import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存储
 *
 * @author CleanCode
 */
public class Storage {
    public static Map<Integer, Socket> userSocketsMap = new ConcurrentHashMap<>(256);
}
