package com.example.sample.task.service.impl;

import com.example.sample.task.constants.State;
import com.example.sample.task.model.Task;
import com.example.sample.task.model.TaskObserver;
import com.example.sample.task.service.TaskExecutor;
import com.example.sample.task.service.TaskManager;
import com.example.sample.util.GenericUtility;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.sample.task.constants.Constants.CONCURRENT_RUN_CAPACITY;
import static com.example.sample.task.constants.State.*;

/**
 * Class to maintain Task Groups Queues.
 */
public class TaskManagerImpl<T> implements TaskManager<T> {

    // Linked List to maintain Tasks Queue
    private final BlockingQueue<Task<T>> taskQueue = new LinkedBlockingQueue<>();

    // Task Observers
    private final Map<UUID, TaskObserver<T>> taskObserverMap = new ConcurrentHashMap<>();

    // Concurrent Hash Map to maintain Future Objects for Tasks
    private final Map<UUID, Future<T>> runningTask = new ConcurrentHashMap<>();
    private final Map<UUID, T> completedTask = new ConcurrentHashMap<>();

    // Concurrent Hash Set to maintain Running Task Groups
    private final Set<UUID> runningTaskGroups = ConcurrentHashMap.newKeySet();

    private static final AtomicInteger RUNNING_TASKS_COUNT = new AtomicInteger(0);

    private final TaskExecutor taskExecutor;

    public TaskManagerImpl (TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void showQueueState() {
        System.out.println("Queued Tasks : " + taskQueue.size());
    }

    /**
     * Adds a Task in the Queue and returns position in Queue.
     * @param task : The Task Needs to be added to Queue
     * @return : The Position of the Task in Queue
     */
    @Override
    public int addTask(Task<T> task) throws InterruptedException {
        if (task == null) {
            return -1;
        }
        TaskObserver<T> observer = new TaskObserver<>(GenericUtility.generateUUID(), this, task);
        task.taskAction().addObserver(observer);
        taskQueue.put(task);
        taskObserverMap.put(task.taskUUID(), observer);
        tryNextTaskExecution();
        return taskQueue.size();
    }

    @Override
    public boolean isAllComplete(long time) {
        if (runningTask.isEmpty()) { return true; }
        boolean resp = runningTask.values().stream().map(Future::isDone).reduce(true, (a,b) -> a && b);
        if (!resp) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        return resp;
    }

    /**
     * Obtain Future of Submitted Task
     * @param task : Obtain Future of Submitted Task
     * @return : The Future of the Task
     */
    @Override
    public T getTaskResponseIfDone(Task<T> task) throws ExecutionException, InterruptedException {
        if (task == null) {
            return null;
        }
        Future<T> future = runningTask.getOrDefault(task.taskUUID(), null);
        if (future == null) {
            System.out.println("Task not Available in Running Queue!");
            return getCompletedTaskResponse(task);
        }
        Thread.sleep(100);
        if (future.isDone()) {
            System.out.println("Obtaining Response of Task " + task.taskUUID());
            return future.get();
        }
        if (future.isCancelled()) {
            System.out.println("Task is Cancelled, ID : " + task.taskUUID());
            return null;
        }
        System.out.println("Task not Complete Yet, ID : " + task.taskUUID());
        return null;
    }

    @Override
    public T getCompletedTaskResponse(Task<T> task) {
        if (task == null) {
            return null;
        }
        return completedTask.getOrDefault(task.taskUUID(), null);
    }

    @Override
    public void handleTaskNotification(Task<T> task, State state) throws ExecutionException, InterruptedException, TimeoutException {
        if (task == null || state == null || State.UNKNOWN.equals(state)) {
            return;
        }
        System.out.println("Task " + task.taskUUID() + " is in State " + state.name());
        if (COMPLETED.equals(state)) {
            completedTask.put(task.taskUUID(), runningTask.get(task.taskUUID()).get(10, TimeUnit.SECONDS));
            runningTask.remove(task.taskUUID());
            runningTaskGroups.remove(task.taskGroup().groupUUID());
            RUNNING_TASKS_COUNT.decrementAndGet();
            System.out.println("Task " + task.taskUUID() + " is Complete with Response " + completedTask.get(task.taskUUID()));
            tryNextTaskExecution();
        }
    }

    /**
     * Tries to execute Task Next in Queue is capacity is not full
     */
    private synchronized void tryNextTaskExecution() {
        // Skip if Capacity is Reached
        if (RUNNING_TASKS_COUNT.get() >= CONCURRENT_RUN_CAPACITY) {
            System.out.println("Execution Capacity is Reached!");
            return;
        }

        // Check if any Task Available from the Queue who's any other task from same Task Group is not Running!
        if (taskQueue.isEmpty() || runningTaskGroups.contains(taskQueue.peek().taskGroup().groupUUID())) {
            System.out.println("No Feasible task Found or Queue is Empty!");
            return;
        }

        // Execute Next Task
        Task<T> nextPossibleTask = taskQueue.poll();
        System.out.println("Running New Task : " + nextPossibleTask.taskUUID() + " | New Count : " + RUNNING_TASKS_COUNT.incrementAndGet());
        runningTaskGroups.add(nextPossibleTask.taskGroup().groupUUID());
        runningTask.put(nextPossibleTask.taskUUID(), taskExecutor.submitTask(nextPossibleTask));
    }

}
