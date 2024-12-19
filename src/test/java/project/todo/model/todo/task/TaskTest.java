package project.todo.model.todo.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import project.todo.exception.todo.DeadlineExceededException;
import project.todo.model.todo.Todo;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TaskTest {
    private Todo todo;

    @BeforeEach
    void setUp() {
        todo = new Todo(
                1L,
                "todo",
                LocalDate.of(2025, 12, 1)
        );
    }

    @DisplayName("주어진 값으로 새로운 Task 객체가 생성된다.")
    @Test
    void initTask() {

        assertDoesNotThrow(() -> new Task(todo, "task"));
    }

    @ValueSource(strings = {" ", "   "})
    @NullAndEmptySource
    @ParameterizedTest(name = "Task 생성 시 내용이 공백이면 예외 발생")
    void initTaskWithEmptyContent(String input) {

        assertThatThrownBy(() -> new Task(todo, input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("내용은 공백일 수 없습니다.");
    }

    @DisplayName("Task 생성 시 마감일을 초과한 경우 예외 발생")
    @Test
    void initTaskWithExceedingDeadline() {
        var exceedDeadline = LocalDate.of(2025, 12, 2).atStartOfDay();

        assertThatThrownBy(() -> new Task(todo, "task", exceedDeadline))
                .isInstanceOf(DeadlineExceededException.class)
                .hasMessage("마감일이 초과되어 Task를 생성할 수 없습니다.");
    }

    @DisplayName("Task 내용을 수정할 수 있다.")
    @Test
    void updateTask() {
        var task = new Task(todo, "task");
        assertThat(task.getContent()).isEqualTo("task");

        task.update("new task");
        assertThat(task.getContent()).isEqualTo("new task");
    }

    @ValueSource(strings = {" ", "   "})
    @NullAndEmptySource
    @ParameterizedTest(name = "Task 내용 수정 시 내용이 공백이면 예외 발생")
    void updateWithEmptyContent(String input) {
        var task = new Task(todo, "task");

        assertThatThrownBy(() -> task.update(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("내용은 공백일 수 없습니다.");
    }

    @DisplayName("이미 완료된 Task 수정 시 예외 발생")
    @Test
    void updateWithAlreadyCompletedTask() {
        var task = new Task(todo, "task");
        task.complete();

        assertThatThrownBy(() -> task.update("new task"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 완료된 Task는 수정할 수 없습니다.");
    }

    @DisplayName("Task 완료 시 완료 상태가 갱신된다.")
    @Test
    void completeTask() {
        var task = new Task(todo, "task");
        assertThat(task.getStatus().isCompleted()).isFalse();

        task.complete();
        assertThat(task.getStatus().isCompleted()).isTrue();
    }

    @DisplayName("이미 완료된 Task에 다시 완료 처리를 시도하면 예외 발생")
    @Test
    void completeWithAlreadyCompletedTask() {
        var task = new Task(todo, "task");
        task.complete();

        assertThatThrownBy(task::complete)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 완료된 Task는 완료 처리할 수 없습니다.");
    }

    @DisplayName("Task 완료 해제 시 완료 해제 상태가 된다.")
    @Test
    void incompleteTask() {
        var task = new Task(todo, "task");
        task.complete();
        assertThat(task.getStatus().isCompleted()).isTrue();

        task.incomplete();
        assertThat(task.getStatus().isCompleted()).isFalse();
    }

    @DisplayName("완료되지 않은 Task에 완료 해제를 시도하면 예외 발생")
    @Test
    void incompleteWithIncompletedTask() {
        var task = new Task(todo, "task");

        assertThatThrownBy(task::incomplete)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("완료되지 않은 Task는 완료 해제할 수 없습니다.");
    }
}
