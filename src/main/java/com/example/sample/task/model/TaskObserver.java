package com.example.sample.task.model;

import com.example.sample.task.constants.State;
import com.example.sample.task.service.TaskManager;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Task Observer.
 *
 * @param observerUUID Unique observer identifier.
 */
public record TaskObserver<T> (UUID observerUUID, TaskManager<T> taskManager, Task<T> task) {
    public TaskObserver {
        if (observerUUID == null || taskManager == null || task == null) {
            throw new IllegalArgumentException("All parameters must not be null");
        }
    }

    public void notify(State state) throws ExecutionException, InterruptedException, TimeoutException {
        System.out.println("State of the Task " + task.taskUUID() + " is " + state.name());
        taskManager.handleTaskNotification(task, state);
    }

}
