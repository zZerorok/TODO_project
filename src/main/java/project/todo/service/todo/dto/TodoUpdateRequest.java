package project.todo.service.todo.dto;

import java.time.LocalDate;

public record TodoUpdateRequest(
        String title,
        LocalDate deadline
) {
}
