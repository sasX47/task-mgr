package com.example.sample.task.service;

import com.example.sample.task.conf.TaskThreadFactory;
import com.example.sample.task.constants.State;
import com.example.sample.task.model.TaskObserver;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public abstract class TaskAction<T> implements Callable<T> {

    private final List<TaskObserver<T>> taskObserverList = new LinkedList<>();

    /**
     * Add a New Observer
     * @param taskObserver : The Task Observer {@link TaskObserver}
     */
    public void addObserver(TaskObserver<T> taskObserver) {
        taskObserverList.add(taskObserver);
    }

    /**
     * Notify all observers registered about state
     * @param state : The current State {@link State}
     */
    protected void notifyObserver(State state) {
        ExecutorService singleThread = Executors.newSingleThreadExecutor(new TaskThreadFactory("Notifier"));
        singleThread.submit(() -> {
            taskObserverList.forEach(observer -> {
                try {
                    observer.notify(state);
                } catch (ExecutionException | InterruptedException | TimeoutException e) {
                    System.out.println("Captures exception " + e.getMessage());
                    e.printStackTrace();
                }
            });
        });
        singleThread.shutdown();
    }

}
