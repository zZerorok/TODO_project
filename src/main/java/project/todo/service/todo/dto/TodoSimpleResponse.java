package project.todo.service.todo.dto;

import project.todo.model.todo.Todo;
import project.todo.model.todo.TodoStatus;

import java.time.LocalDate;

public record TodoSimpleResponse(
        Long id,
        String title,
        LocalDate deadline,
        TodoStatus status
) {

    public static TodoSimpleResponse from(Todo todo) {
        return new TodoSimpleResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDeadline().toLocalDate(),
                todo.getStatus()
        );
    }
}
