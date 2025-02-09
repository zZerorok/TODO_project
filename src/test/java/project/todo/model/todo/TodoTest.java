package project.todo.model.todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import project.todo.exception.member.MemberException;
import project.todo.exception.todo.DeadlineExceededException;
import project.todo.exception.todo.DeadlineException;
import project.todo.exception.todo.TodoStateException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TodoTest {
    private Todo todo;

    @BeforeEach
    void setUp() {
        todo = new Todo(
                1L,
                "프로젝트",
                LocalDate.of(2025, 12, 1)
        );
    }

    @DisplayName("주어진 값으로 새로운 Todo 객체가 생성된다.")
    @Test
    void initTodo() {

        assertDoesNotThrow(() -> {
            new Todo(
                    1L,
                    "프로젝트",
                    LocalDate.of(2025, 12, 1)
            );
        });
    }

    @ValueSource(strings = {" ", "   "})
    @NullAndEmptySource
    @ParameterizedTest(name = "Todo 생성 시 제목이 공백이면 예외 발생")
    void initTodoWithEmptyTitle(String input) {

        assertThatThrownBy(() -> {
            new Todo(
                    1L,
                    input,
                    LocalDate.of(2025, 12, 1)
            );
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제목은 공백일 수 없습니다.");
    }

    @DisplayName("Todo 생성 시 마감일이 비어있으면 예외 발생")
    @Test
    void initTodoWithEmptyDeadline() {

        assertThatThrownBy(() -> {
            new Todo(
                    1L,
                    "프로젝트",
                    null,
                    LocalDateTime.now()
            );
        })
                .isInstanceOf(DeadlineException.class)
                .hasMessage("마감일을 설정해주세요.");
    }

    @DisplayName("Todo 생성 시 마감일이 현재보다 과거일 경우 예외 발생")
    @Test
    void initTodoWithPastDeadline() {

        assertThatThrownBy(() -> {
            new Todo(
                    1L,
                    "프로젝트",
                    LocalDate.of(2024, 12, 1)
            );
        })
                .isInstanceOf(DeadlineExceededException.class)
                .hasMessage("마감일은 현재보다 과거일 수 없습니다.");
    }

    @DisplayName("Todo 제목을 수정할 수 있다.")
    @Test
    void updateTitle() {
        assertThat(todo.getTitle()).isEqualTo("프로젝트");

        todo.update("최종 프로젝트", null);

        assertThat(todo.getTitle()).isEqualTo("최종 프로젝트");
    }

    @DisplayName("Todo 마감일을 수정할 수 있다.")
    @Test
    void updateDeadline() {
        var beforeDeadline = LocalDate.of(2025, 12, 1)
                .atTime(LocalTime.MAX);
        assertThat(todo.getDeadline()).isEqualTo(beforeDeadline);

        var newDeadline = LocalDate.of(2025, 12, 2);
        var afterDeadline = newDeadline.atTime(LocalTime.MAX);
        todo.update(null, newDeadline);

        assertThat(todo.getDeadline()).isEqualTo(afterDeadline);
    }

    @DisplayName("Todo 제목과 마감일을 한 번에 수정할 수 있다.")
    @Test
    void updateTitleAndDeadline() {
        var beforeDeadline = LocalDate.of(2025, 12, 1)
                .atTime(LocalTime.MAX);
        assertThat(todo.getTitle()).isEqualTo("프로젝트");
        assertThat(todo.getDeadline()).isEqualTo(beforeDeadline);

        var newDeadline = LocalDate.of(2025, 12, 2);
        todo.update("최종 프로젝트", newDeadline);

        assertThat(todo.getTitle()).isEqualTo("최종 프로젝트");
        assertThat(todo.getDeadline()).isEqualTo(newDeadline.atTime(LocalTime.MAX));
    }

    @DisplayName("이미 완료된 Todo 수정 시 예외 발생")
    @Test
    void updateWithAlreadyCompletedTodo() {
        todo.complete();

        assertThatThrownBy(() -> {
            todo.update("프로젝트2", LocalDate.of(2025, 1, 1));
        })
                .isInstanceOf(TodoStateException.class)
                .hasMessage("이미 완료된 Todo는 수정할 수 없습니다.");
    }

    @DisplayName("Todo 수정 시 마감일이 초과된 경우 예외 발생")
    @Test
    void updateWithExceedingDeadline() {
        var deadline = LocalDate.of(2024, 2, 1)
                .atTime(LocalTime.MAX);
        var createdAt = LocalDate.of(2024, 1, 1)
                .atTime(LocalTime.MIN);
        var newTodo = new Todo(
                1L,
                "프로젝트",
                deadline,
                createdAt
        );

        assertThatThrownBy(() -> {
            newTodo.update("최종 프로젝트", LocalDate.of(2025, 1, 1));
        })
                .isInstanceOf(DeadlineExceededException.class)
                .hasMessage("마감일이 초과되어 수정할 수 없습니다.");
    }

    @DisplayName("Todo 수정 시 값이 없으면 Todo는 수정되지 않는다.")
    @Test
    void updateWithNothing() {
        var beforeDeadline = todo.getDeadline();
        var beforeTitle = todo.getTitle();

        todo.update(null, null);

        assertThat(todo.getTitle()).isEqualTo(beforeTitle);
        assertThat(todo.getDeadline()).isEqualTo(beforeDeadline);
    }

    @DisplayName("Todo 완료 후 완료 상태로 변경된다.")
    @Test
    void completeTodo() {
        assertThat(todo.getStatus().isCompleted()).isFalse();

        todo.complete();

        assertThat(todo.getStatus().isCompleted()).isTrue();
    }

    @DisplayName("완료된 Todo에 완료를 시도하면 예외 발생")
    @Test
    void completeWithAlreadyCompletedTodo() {
        todo.complete();

        assertThatThrownBy(() -> todo.complete())
                .isInstanceOf(TodoStateException.class)
                .hasMessage("이미 완료된 Todo는 완료 처리할 수 없습니다.");
    }

    @DisplayName("Todo 완료 해제 후 완료 해제 상태로 변경된다.")
    @Test
    void incompleteTodo() {
        todo.complete();
        assertThat(todo.getStatus().isCompleted()).isTrue();

        todo.incomplete();

        assertThat(todo.getStatus().isCompleted()).isFalse();
    }

    @DisplayName("완료되지 않은 Todo에 완료 해제를 시도하면 예외 발생")
    @Test
    void incompleteWithIncompletedTodo() {

        assertThatThrownBy(() -> todo.incomplete())
                .isInstanceOf(TodoStateException.class)
                .hasMessage("완료되지 않은 Todo는 해제할 수 없습니다.");
    }

    @DisplayName("Todo 작성자가 아니면 예외 발생")
    @Test
    void ownerException() {
        var todo = new Todo(
                1L,
                "제목",
                LocalDate.now()
        );

        assertThatThrownBy(() -> todo.validateWriter(5L))
                .isInstanceOf(MemberException.class);
    }
}
