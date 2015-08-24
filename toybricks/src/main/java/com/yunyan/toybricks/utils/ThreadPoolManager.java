package com.yunyan.toybricks.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/**
 * Created by George on 2015/3/27.
 */
public class ThreadPoolManager {
    private ExecutorService service;

    private ThreadPoolManager() {
        int num = Runtime.getRuntime().availableProcessors();
        service = Executors.newFixedThreadPool(num * 4);
    }

    private static final ThreadPoolManager manager = new ThreadPoolManager();

    public static ThreadPoolManager getInstance() {
        return manager;
    }

    public void addTask(Runnable runnable) {
        service.execute(runnable);
    }
}
