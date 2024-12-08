package project.todo.model.todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.todo.model.member.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TodoTest {
    private Todo todo;

    @BeforeEach
    void setUp() {
        todo = new Todo(
                new Member("member"),
                "프로젝트",
                LocalDate.of(2024, 12, 25)
        );
    }

    @DisplayName("주어진 값으로 Todo 객체가 생성된다.")
    @Test
    void initTodo() {

        assertThat(todo.getMember().getName()).isEqualTo("member");
        assertThat(todo.getTitle()).isEqualTo("프로젝트");
        assertThat(todo.getDeadline()).isEqualTo(LocalDate.of(2024, 12, 25));
        assertThat(todo.isCompleted()).isFalse();
    }

    @DisplayName("Todo의 제목이 변경된다.")
    @Test
    void updateTitle() {
        var request = new TodoUpdateRequest("프로젝트2", null);
        todo.updateTitle(request.title());

        assertThat(todo.getTitle()).isEqualTo(request.title());
    }

    @DisplayName("Todo의 마감일이 변경된다.")
    @Test
    void updateDeadline() {
        var request = new TodoUpdateRequest(null,
                LocalDate.of(2025, 1, 1));
        todo.updateDeadline(request.deadline());

        assertThat(todo.getDeadline()).isEqualTo(request.deadline());
    }

    @DisplayName("Todo의 제목과 마감일을 한 번에 변경할 수 있다.")
    @Test
    void updateTitleAndDeadline() {
        var request = new TodoUpdateRequest("프로젝트2",
                LocalDate.of(2025, 1, 1));
        todo.updateFrom(request);

        assertThat(todo.getTitle()).isEqualTo(request.title());
        assertThat(todo.getDeadline()).isEqualTo(request.deadline());
    }

    @DisplayName("이미 완료된 Todo를 수정할 때 예외 발생")
    @Test
    void updateWithAlreadyCompletedTodo() {
        todo.complete();
        var request = new TodoUpdateRequest("프로젝트2",
                LocalDate.of(2025, 1, 1));

        assertThatThrownBy(() -> todo.updateFrom(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 완료된 Todo는 수정할 수 없습니다.");
    }

    @DisplayName("Todo 완료 시 완료 상태 및 완료 시간이 갱신된다")
    @Test
    void completeTodo() {
        todo.complete();
        var now = LocalDateTime.now();

        assertThat(todo.isCompleted()).isTrue();
        assertThat(todo.getCompletedAt()).isBetween(now.minusSeconds(1), now.plusSeconds(1));
    }

    @DisplayName("이미 완료된 Todo에 다시 완료를 시도하면 예외 발생")
    @Test
    void completeWithAlreadyCompletedTodo() {
        todo.complete();

        assertThatThrownBy(() -> todo.complete())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 완료된 Todo 입니다.");
    }
}
