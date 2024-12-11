package project.todo.model.todo;

import java.time.LocalDate;

public record TodoCreateRequest(
        String title,
        LocalDate deadLine
) {
}
