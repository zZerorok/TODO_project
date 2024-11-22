package project.todo.model.todo;

import java.time.LocalDate;

public record TodoCreateRequest(
        Long memberId,
        String title,
        LocalDate deadLine
) {
}
