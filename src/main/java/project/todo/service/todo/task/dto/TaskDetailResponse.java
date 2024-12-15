package project.todo.service.todo.task.dto;

import project.todo.model.todo.task.Task;
import project.todo.model.todo.task.TaskStatus;

import java.time.LocalDateTime;

public record TaskDetailResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        TaskStatus status,
        LocalDateTime completedAt
) {

    public static TaskDetailResponse from(Task task) {
        return new TaskDetailResponse(
                task.getId(),
                task.getContent(),
                task.getCreatedAt(),
                task.getStatus(),
                task.getCompletedAt()
        );
    }
}
