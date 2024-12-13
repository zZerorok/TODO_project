package project.todo.service.todo.task;

import project.todo.model.todo.task.Task;

import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        boolean isCompleted,
        LocalDateTime completedAt
) {

    public static TaskResponse from(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getContent(),
                task.getCreatedAt(),
                task.isCompleted(),
                task.getCompletedAt()
        );
    }
}
