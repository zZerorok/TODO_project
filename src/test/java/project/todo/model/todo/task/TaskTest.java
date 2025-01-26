package project.todo.model.todo.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import project.todo.exception.todo.DeadlineExceededException;
import project.todo.exception.todo.task.TaskStateException;
import project.todo.model.todo.Todo;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class TaskTest {
    private Todo todo;
    private Task task;

    @BeforeEach
    void setUp() {
        todo = new Todo(
                1L,
                "todo",
                LocalDate.of(2025, 12, 1)
        );

        task = new Task(
                todo,
                "task"
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
        var exceedDeadline = LocalDate.of(2025, 12, 2)
                .atTime(LocalTime.MIN);

        assertThatThrownBy(() -> {
            new Task(
                    todo,
                    "task",
                    exceedDeadline
            );
        })
                .isInstanceOf(DeadlineExceededException.class)
                .hasMessage("마감일이 초과되어 Task를 생성할 수 없습니다.");
    }

    @DisplayName("Task 내용을 수정할 수 있다.")
    @Test
    void updateTask() {
        assertThat(task.getContent()).isEqualTo("task");

        task.update("new task");

        assertThat(task.getContent()).isEqualTo("new task");
    }

    @ValueSource(strings = {" ", "   "})
    @NullAndEmptySource
    @ParameterizedTest(name = "Task 수정 시 내용이 공백이면 예외 발생")
    void updateTaskWithEmptyContent(String input) {

        assertThatThrownBy(() -> task.update(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("내용은 공백일 수 없습니다.");
    }

    @DisplayName("Task 수정 시 완료 상태면 예외 발생")
    @Test
    void updateWithAlreadyCompletedTask() {
        task.complete();

        assertThatThrownBy(() -> task.update("new task"))
                .isInstanceOf(TaskStateException.class)
                .hasMessage("이미 완료된 Task는 수정할 수 없습니다.");
    }

    @DisplayName("Task 완료 후 완료 상태로 변경된다.")
    @Test
    void completeTask() {
        assertThat(task.getStatus().isCompleted()).isFalse();

        task.complete();

        assertThat(task.getStatus().isCompleted()).isTrue();
    }

    @DisplayName("완료된 Task에 완료를 시도하면 예외 발생")
    @Test
    void completeWithAlreadyCompletedTask() {
        task.complete();

        assertThatThrownBy(task::complete)
                .isInstanceOf(TaskStateException.class)
                .hasMessage("이미 완료된 Task는 완료 처리할 수 없습니다.");
    }

    @DisplayName("Task 완료 해제 후 완료 해제 상태로 변경된다.")
    @Test
    void incompleteTask() {
        task.complete();
        assertThat(task.getStatus().isCompleted()).isTrue();

        task.incomplete();
        assertThat(task.getStatus().isCompleted()).isFalse();
    }

    @DisplayName("완료되지 않은 Task에 완료 해제를 시도하면 예외 발생")
    @Test
    void incompleteWithIncompletedTask() {

        assertThatThrownBy(task::incomplete)
                .isInstanceOf(TaskStateException.class)
                .hasMessage("완료되지 않은 Task는 완료 해제할 수 없습니다.");
    }
}
