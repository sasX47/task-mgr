package com.example.sample.task.service.impl;

import com.example.sample.task.model.Task;
import com.example.sample.task.service.TaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class TaskExecutorImpl implements TaskExecutor {

    private final ExecutorService executorService;

    public TaskExecutorImpl(ExecutorService executorService) {
        this.executorService = executorService;
    }


    @Override
    public <T> Future<T> submitTask(Task<T> task) {
        System.out.println("Executing Task : " + task.taskUUID());
        Future<T> resp = executorService.submit(task.taskAction());
        return resp;
    }

}
