package project.todo.service.todo;

import java.time.LocalDate;

public record TodoCreateRequest(
        String title,
        LocalDate deadLine
) {
}
