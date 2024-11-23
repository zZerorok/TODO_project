package project.todo.model.todo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TodoResponse(
        Long id,
        String title,
        LocalDateTime createdAt,
        LocalDate deadline,
        boolean isCompleted,
        LocalDateTime completedAt
) {

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getCreatedAt(),
                todo.getDeadline(),
                todo.isCompleted(),
                todo.getCompletedAt()
        );
    }
}
