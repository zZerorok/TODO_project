package project.todo.service.todo;

import project.todo.model.todo.Todo;
import project.todo.model.todo.TodoStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TodoResponse(
        Long id,
        String title,
        LocalDateTime createdAt,
        LocalDate deadline,
        TodoStatus status,
        LocalDateTime completedAt
) {

    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getCreatedAt(),
                todo.getDeadline().toLocalDate(),
                todo.getStatus(),
                todo.getCompletedAt()
        );
    }
}
