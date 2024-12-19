package project.todo.model.todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

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

    @DisplayName("Todo 생성 시 null 입력하면 예외 발생")
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
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("마감일을 설정해주세요.");
    }

    @DisplayName("Todo 생성 시 마감일이 현재보다 과거일 경우 예외 발생")
    @Test
    void initTodoWithPastDeadline() {
        var deadline = LocalDate.of(2024, 12, 1);

        assertThatThrownBy(() -> {
            new Todo(
                    1L,
                    "프로젝트",
                    deadline
            );
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("마감일은 현재보다 과거일 수 없습니다.");
    }

    @DisplayName("Todo의 제목을 변경할 수 있다.")
    @Test
    void updateTitle() {
        var deadline = LocalDate.of(2025, 12, 1)
                .atTime(LocalTime.MAX);
        assertThat(todo.getTitle()).isEqualTo("프로젝트");
        assertThat(todo.getDeadline()).isEqualTo(deadline);

        todo.update(
                "최종 프로젝트",
                null
        );
        assertThat(todo.getTitle()).isEqualTo("최종 프로젝트");
        assertThat(todo.getDeadline()).isEqualTo(deadline);
    }

    @DisplayName("Todo의 마감일을 변경할 수 있다.")
    @Test
    void updateDeadline() {
        var beforeDeadline = LocalDate.of(2025, 12, 1).atTime(LocalTime.MAX);
        assertThat(todo.getTitle()).isEqualTo("프로젝트");
        assertThat(todo.getDeadline()).isEqualTo(beforeDeadline);

        var newDeadline = LocalDate.of(2025, 12, 2);
        todo.update(
                null,
                newDeadline
        );
        assertThat(todo.getTitle()).isEqualTo("프로젝트");
        assertThat(todo.getDeadline()).isEqualTo(newDeadline.atTime(LocalTime.MAX));
    }

    @DisplayName("Todo의 제목과 마감일을 한 번에 변경할 수 있다.")
    @Test
    void updateTitleAndDeadline() {
        var beforeDeadline = LocalDate.of(2025, 12, 1)
                .atTime(LocalTime.MAX);
        assertThat(todo.getTitle()).isEqualTo("프로젝트");
        assertThat(todo.getDeadline()).isEqualTo(beforeDeadline);

        var newDeadline = LocalDate.of(2025, 12, 2);
        todo.update(
                "최종 프로젝트",
                newDeadline
        );
        assertThat(todo.getTitle()).isEqualTo("최종 프로젝트");
        assertThat(todo.getDeadline()).isEqualTo(newDeadline.atTime(LocalTime.MAX));
    }

    @DisplayName("이미 완료된 Todo를 수정할 때 예외 발생")
    @Test
    void updateWithAlreadyCompletedTodo() {
        todo.complete();

        assertThatThrownBy(() -> {
            todo.update(
                    "프로젝트2",
                    LocalDate.of(2025, 1, 1)
            );
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 완료된 Todo는 수정할 수 없습니다.");
    }

    @DisplayName("Todo 수정 시 마감일이 초과된 경우 예외 발생")
    @Test
    void updateWithExceedingDeadline() {
        var deadline = LocalDate.of(2024, 2, 1)
                .atTime(LocalTime.MAX);
        var createdAt = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
        var newTodo = new Todo(
                1L,
                "프로젝트",
                deadline,
                createdAt
        );

        assertThatThrownBy(() -> {
            newTodo.update(
                    "최종 프로젝트",
                    LocalDate.of(2025, 1, 1)
            );
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("마감일이 초과되어 수정할 수 없습니다.");
    }

    @DisplayName("Todo 수정 시 아무 값도 없을 경우 수정되지 않는다.")
    @Test
    void updateWithNothing() {
        var beforeDeadline = todo.getDeadline();
        var beforeTitle = todo.getTitle();
        todo.update(null, null);

        assertThat(todo.getTitle()).isEqualTo(beforeTitle);
        assertThat(todo.getDeadline()).isEqualTo(beforeDeadline);
    }

    @DisplayName("Todo 완료 시 완료 상태가 갱신된다")
    @Test
    void completeTodo() {
        todo.complete();

        assertThat(todo.getStatus().isCompleted()).isTrue();
    }

    @DisplayName("이미 완료된 Todo에 다시 완료 처리를 시도하면 예외 발생")
    @Test
    void completeWithAlreadyCompletedTodo() {
        todo.complete();

        assertThatThrownBy(() -> todo.complete())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 완료된 Todo는 완료 처리할 수 없습니다.");
    }

    @DisplayName("Todo 완료 해제 시 완료 해제 상태가 된다.")
    @Test
    void incompleteTodo() {
        todo.complete();
        todo.incomplete();

        assertThat(todo.getStatus().isCompleted()).isFalse();
    }

    @DisplayName("완료되지 않은 Todo에 완료 해제 처리를 시도하면 예외 발생")
    @Test
    void incompleteWithIncompletedTodo() {

        assertThatThrownBy(() -> todo.incomplete())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("완료되지 않은 Todo는 해제할 수 없습니다.");
    }
}
