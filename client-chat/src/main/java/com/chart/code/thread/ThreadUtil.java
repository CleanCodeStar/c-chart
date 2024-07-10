package com.chart.code.thread;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 写点注释
 * <br/>
 * Created in 2019-08-09 15:37
 *
 * @author Zhenfeng Li
 */
public class ThreadUtil {
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(4, 4, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), new ThreadPoolExecutor.CallerRunsPolicy());


    /**
     * 使用线程池
     */
    public static ThreadPoolExecutor getExecutor() {
        return EXECUTOR;
    }

    /**
     * 向线程管理器添加线程
     */
    public static void addRunnable(Runnable lzfThread) {
        EXECUTOR.submit(lzfThread);
    }
}
