package project.todo.service.todo;

import java.time.LocalDate;

public record TodoUpdateRequest(
        String title,
        LocalDate deadline
) {
}
