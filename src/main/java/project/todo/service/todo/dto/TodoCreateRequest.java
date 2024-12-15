package project.todo.service.todo.dto;

import java.time.LocalDate;

public record TodoCreateRequest(
        String title,
        LocalDate deadLine
) {
}
