package com.example.juexingzhe.jueapp.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程工作
 */
public class WorkerManager {

    private static WorkerManager manager;
    private ExecutorService executorService;

    private WorkerManager() {
        if (this.executorService == null) {
            executorService = Executors.newFixedThreadPool(3);

        }
    }

    public static WorkerManager getInstance() {
        if (manager == null) {
            synchronized (WorkerManager.class) {
                if (manager == null) {
                    manager = new WorkerManager();
                }
            }
        }

        return manager;
    }

    public void postTask(Runnable task) {
        executorService.execute(task);
    }

}
