package project.todo.service.todo.task.dto;

import project.todo.model.todo.Status;
import project.todo.model.todo.task.Task;

import java.time.LocalDateTime;

public record TaskDetailResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        Status taskStatus
) {

    public static TaskDetailResponse from(Task task) {
        return new TaskDetailResponse(
                task.getId(),
                task.getContent(),
                task.getCreatedAt(),
                task.getStatus()
        );
    }
}
