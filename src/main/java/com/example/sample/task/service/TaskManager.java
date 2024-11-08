package com.example.sample.task.service;

import com.example.sample.task.constants.State;
import com.example.sample.task.model.Task;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface TaskManager<T> {

    /**
     * Shows the current state of Queue
     */
    void showQueueState();

    /**
     * Adds a Task in the Queue and returns position in Queue.
     * @param task : The Task Needs to be added to Queue
     * @return : The Position of the Task in Queue
     */
    int addTask(Task<T> task) throws InterruptedException;

    /**
     * Check if All tasks are complete
     */
    boolean isAllComplete(long time);

    /**
     * Obtain Future of Submitted Task
     * @param task : Obtain Future of Submitted Task
     * @return : The Future of the Task
     */
    T getTaskResponseIfDone(Task<T> task) throws ExecutionException, InterruptedException;

    /**
     * Obtain Response of Submitted Task if Completed
     * @param task : Obtain Future of Submitted Task
     * @return : The Future of the Task
     */
    T getCompletedTaskResponse(Task<T> task);

    /**
     * Handle Task Notifications
     * @param task : Obtain Future of Submitted Task
     * @param state : The Task State
     */
    void handleTaskNotification(Task<T> task, State state) throws ExecutionException, InterruptedException, TimeoutException;

}
