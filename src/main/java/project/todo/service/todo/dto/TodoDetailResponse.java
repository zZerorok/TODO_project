package project.todo.service.todo.dto;

import project.todo.model.todo.Todo;
import project.todo.model.todo.TodoStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TodoDetailResponse(
        Long id,
        String title,
        LocalDate deadline,
        LocalDateTime createdAt,
        TodoStatus status
) {

    public static TodoDetailResponse from(Todo todo) {
        return new TodoDetailResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDeadline().toLocalDate(),
                todo.getCreatedAt(),
                todo.getStatus()
        );
    }
}
