package project.todo.model.todo;

import java.time.LocalDate;

public record TodoUpdateRequest(
        String title,
        LocalDate deadline
) {
}
