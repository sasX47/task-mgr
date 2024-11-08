package com.example.sample;

import com.example.sample.task.conf.TaskThreadFactory;
import com.example.sample.task.constants.TaskType;
import com.example.sample.task.model.Task;
import com.example.sample.task.model.TaskGroup;
import com.example.sample.task.service.TaskExecutor;
import com.example.sample.task.service.TaskManager;
import com.example.sample.task.service.impl.TaskActionImpl;
import com.example.sample.task.service.impl.TaskExecutorImpl;
import com.example.sample.task.service.impl.TaskManagerImpl;
import com.example.sample.util.GenericUtility;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.sample.task.constants.Constants.CONCURRENT_RUN_CAPACITY;

public class Main {

    public static void main(String... args) {
        TaskGroup tg1 = new TaskGroup(GenericUtility.generateUUID());
        TaskGroup tg2 = new TaskGroup(GenericUtility.generateUUID());
        TaskGroup tg3 = new TaskGroup(GenericUtility.generateUUID());
        TaskGroup tg4 = new TaskGroup(GenericUtility.generateUUID());

        Task<String> t11 = new Task<>(GenericUtility.generateUUID(), tg1, TaskType.READ, new TaskActionImpl<>("T11"));
        Task<String> t12 = new Task<>(GenericUtility.generateUUID(), tg1, TaskType.WRITE, new TaskActionImpl<>("T12"));

        Task<String> t21 = new Task<>(GenericUtility.generateUUID(), tg2, TaskType.READ, new TaskActionImpl<>("T21"));
        Task<String> t22 = new Task<>(GenericUtility.generateUUID(), tg2, TaskType.WRITE, new TaskActionImpl<>("T22"));
        Task<String> t23 = new Task<>(GenericUtility.generateUUID(), tg2, TaskType.READ, new TaskActionImpl<>("T23"));

        Task<String> t31 = new Task<>(GenericUtility.generateUUID(), tg3, TaskType.READ, new TaskActionImpl<>("T31"));
        Task<String> t32 = new Task<>(GenericUtility.generateUUID(), tg3, TaskType.WRITE, new TaskActionImpl<>("T32"));
        Task<String> t33 = new Task<>(GenericUtility.generateUUID(), tg3, TaskType.READ, new TaskActionImpl<>("T33"));
        Task<String> t34 = new Task<>(GenericUtility.generateUUID(), tg3, TaskType.WRITE, new TaskActionImpl<>("T34"));

        Task<String> t41 = new Task<>(GenericUtility.generateUUID(), tg4, TaskType.READ, new TaskActionImpl<>("T41"));
        Task<String> t42 = new Task<>(GenericUtility.generateUUID(), tg4, TaskType.WRITE, new TaskActionImpl<>("T42"));
        Task<String> t43 = new Task<>(GenericUtility.generateUUID(), tg4, TaskType.READ, new TaskActionImpl<>("T43"));
        Task<String> t44 = new Task<>(GenericUtility.generateUUID(), tg4, TaskType.READ, new TaskActionImpl<>("T44"));
        Task<String> t45 = new Task<>(GenericUtility.generateUUID(), tg4, TaskType.WRITE, new TaskActionImpl<>("T45"));

        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENT_RUN_CAPACITY, new TaskThreadFactory());
        TaskExecutor taskExecutor = new TaskExecutorImpl(executorService);
        TaskManager<String> taskManager = new TaskManagerImpl<>(taskExecutor);

        List<Task<String>> taskList = List.of(t11, t21, t22, t12, t23, t31, t42, t33, t44, t41, t32, t43, t34, t45);
        System.out.println("Task Details");
        taskList.forEach(System.out::println);

        taskManager.showQueueState();
        taskList.forEach(task -> {
            try {
                taskManager.addTask(task);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        taskManager.showQueueState();

        System.out.print("Waiting for all tasks to complete...");
        while (!taskManager.isAllComplete(1000L)) {
            System.out.print(".");
        }

        taskList.forEach(task -> {
            String resp = taskManager.getCompletedTaskResponse(task);
            System.out.println("Task : " + task.taskUUID() + " | Resp : " + (resp == null ? "NULL" : resp));
        });
        executorService.shutdown();
        System.out.println("DONE!");
    }

}

