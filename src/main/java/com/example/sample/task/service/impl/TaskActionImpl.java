package com.example.sample.task.service.impl;

import com.example.sample.task.constants.State;
import com.example.sample.task.service.TaskAction;

import java.util.concurrent.ThreadLocalRandom;

public class TaskActionImpl<T> extends TaskAction<T> {

    private final String name;

    public TaskActionImpl(String name) {
        if (name == null) {
            name = "DEFAULT ACTION";
        }
        this.name = name.toUpperCase();
    }

    @Override
    public T call() throws Exception {
        this.notifyObserver(State.STARTED);
        System.out.println(name + " Starting Execution on " + Thread.currentThread().getName());

        int x = ThreadLocalRandom.current().nextInt(500, 5001);
        System.out.println(name + " Waiting for " + x +  " on thread " + Thread.currentThread().getName());
        this.notifyObserver(State.EXECUTION);
        Thread.sleep(x);

        System.out.println(name + " Completing Execution on " + Thread.currentThread().getName());
        this.notifyObserver(State.COMPLETED);
        return (T) (name + "-" + Thread.currentThread().getName() + "-" + x);
    }

}
