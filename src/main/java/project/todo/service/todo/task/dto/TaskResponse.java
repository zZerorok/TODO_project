package project.todo.service.todo.task.dto;

import project.todo.model.todo.task.Task;
import project.todo.model.todo.task.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        TaskStatus status,
        LocalDateTime completedAt
) {

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getContent(),
                task.getCreatedAt(),
                task.getStatus(),
                task.getCompletedAt()
        );
    }
}
