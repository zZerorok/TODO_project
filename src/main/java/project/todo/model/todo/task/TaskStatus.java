package project.todo.model.todo.task;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TaskStatus {
    COMPLETED("완료"),
    INCOMPLETE("미완료");

    private final String status;

    public boolean isCompleted() {
        return this == COMPLETED;
    }
}
