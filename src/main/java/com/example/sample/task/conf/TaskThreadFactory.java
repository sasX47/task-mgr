package com.example.sample.task.conf;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskThreadFactory implements ThreadFactory {

    private static final String DEFAULT_THREAD_NAME_PREFIX = "task-thread-";
    private static final AtomicInteger ID = new AtomicInteger(0);

    private final String threadNamePrefix;

    public TaskThreadFactory(String threadNamePrefix) {
        this.threadNamePrefix = threadNamePrefix;
    }
    public TaskThreadFactory() {
        this(DEFAULT_THREAD_NAME_PREFIX);
    }

    public Thread newThread(Runnable runnable) {
        return new Thread(runnable, threadNamePrefix + ID.incrementAndGet());
    }

}
