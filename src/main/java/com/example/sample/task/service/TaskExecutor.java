package com.example.sample.task.service;

import com.example.sample.task.model.Task;

import java.util.concurrent.Future;

public interface TaskExecutor {
    /**
     * Submit new task to be queued and executed.
     *
     * @param task Task to be executed by the executor. Must not be null.
     * @return Future for the task asynchronous computation result.
     */
    <T> Future<T> submitTask(Task<T> task);
}
