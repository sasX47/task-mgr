package com.example.sample.task.model;

import com.example.sample.task.constants.TaskType;
import com.example.sample.task.service.TaskExecutor;
import com.example.sample.task.service.TaskAction;

import java.util.UUID;

/**
 * Representation of computation to be performed by the {@link TaskExecutor}.
 *
 * @param taskUUID Unique task identifier.
 * @param taskGroup Task group.
 * @param taskType Task type.
 * @param taskAction Callable representing task computation and returning the result.
 * @param <T> Task computation result value type.
 */
public record Task<T>(
        UUID taskUUID,
        TaskGroup taskGroup,
        TaskType taskType,
        TaskAction<T> taskAction // Modified Task Action to have Observable behaviour
) {
    public Task {
        if (taskUUID == null || taskGroup == null || taskType == null || taskAction == null) {
            throw new IllegalArgumentException("All parameters must not be null");
        }
    }

}
