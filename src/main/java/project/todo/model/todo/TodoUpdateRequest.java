package project.todo.model.todo;

import project.todo.util.DateUtils;

import java.time.LocalDate;
import java.util.Objects;

public record TodoUpdateRequest(
        String title,
        LocalDate deadline
) {

    public void updateTo(Todo todo) {
        validateComplete(todo);

        if (title != null) {
            todo.changeTitle(title);
        }

        if (deadline != null) {
            todo.changeDeadline(DateUtils.toEndOfDay(deadline));
        }
    }

    private void validateComplete(Todo todo) {
        if (todo.isCompleted()) {
            throw new IllegalArgumentException("이미 완료된 Todo는 수정할 수 없습니다.");
        }
    }

    public boolean isChanged(Todo todo) {
        if (!title.equals(todo.getTitle())) {
            return true;
        }

        return !Objects.equals(DateUtils.toEndOfDay(deadline), todo.getDeadline());
    }
}
