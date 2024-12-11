package project.todo.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.todo.model.member.Member;
import project.todo.model.member.MemberRepository;
import project.todo.model.todo.Todo;
import project.todo.model.todo.TodoCreateRequest;
import project.todo.model.todo.TodoRepository;
import project.todo.model.todo.TodoUpdateRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class TodoServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TodoService todoService;

    @BeforeEach
    void setUp() {
        var member = new Member("사용자");
        memberRepository.save(member);

        var todos = List.of(
                new Todo(
                        member,
                        "프로젝트1",
                        LocalDate.of(2024, 9, 1)
                ),
                new Todo(
                        member,
                        "프로젝트2",
                        LocalDate.of(2024, 10, 11)
                ),
                new Todo(
                        member,
                        "프로젝트3",
                        LocalDate.of(2024, 11, 27)
                )
        );
        todoRepository.saveAll(todos);
    }

    @DisplayName("사용자가 작성한 Todo를 조회할 수 있다.")
    @Test
    void findTodos() {
        var member = memberRepository.findAll().get(0);
        var todos = todoService.findTodos(member.getId());

        assertThat(todos).hasSize(3);
        assertThat(todos.get(0).title()).isEqualTo("프로젝트1");
    }

    @DisplayName("Todo 조회 시 Todo가 없을 경우 예외 발생")
    @Test
    void findWithEmptyTodos() {
        var member = memberRepository.findAll().get(0);
        todoRepository.deleteAll();

        assertThatThrownBy(() -> todoService.findTodos(member.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("작성하신 Todo가 없습니다.");
    }

    @DisplayName("사용자는 새로운 Todo를 작성할 수 있다.")
    @Test
    void createTodo() {
        var member = memberRepository.findAll().get(0);
        var newTodo = new TodoCreateRequest(
                "프로젝트4",
                LocalDate.of(2025, 1, 1)
        );
        todoService.create(member.getId(), newTodo);
        var todos = todoRepository.findAll();

        assertThat(todos).hasSize(4);
        assertThat(todos.get(3).getTitle()).isEqualTo("프로젝트4");
        assertThat(todos.get(3).getDeadline()).isEqualTo(LocalDate.of(2025, 1, 1));
    }

    @DisplayName("Todo의 제목과 마감일을 수정할 수 있다.")
    @Test
    void updateAll() {
        var todo = todoRepository.findAll().get(0);
        var request = new TodoUpdateRequest(
                "최종 프로젝트",
                LocalDate.of(2025, 1, 1)
        );
        todoService.updateAll(todo.getId(), request);
        var updatedTodo = todoRepository.findById(todo.getId())
                .orElseThrow(() -> new EntityNotFoundException("Todo가 없습니다."));

        assertThat(updatedTodo.getTitle()).isEqualTo("최종 프로젝트");
        assertThat(updatedTodo.getDeadline()).isEqualTo(LocalDate.of(2025, 1, 1));
    }

    @DisplayName("Todo의 제목만 수정할 수 있다.")
    @Test
    void updateTitle() {
        var todo = todoRepository.findAll().get(0);
        var beforeDeadline = todo.getDeadline();
        var request = new TodoUpdateRequest(
                "최종 프로젝트",
                null
        );
        todoService.update(todo.getId(), request);
        var updatedTodo = todoRepository.findById(todo.getId())
                .orElseThrow(() -> new EntityNotFoundException("Todo가 없습니다."));

        assertThat(updatedTodo.getTitle()).isEqualTo("최종 프로젝트");
        assertThat(updatedTodo.getDeadline()).isEqualTo(beforeDeadline);
    }

    @DisplayName("Todo의 마감일만 수정할 수 있다.")
    @Test
    void updateDeadline() {
        var todo = todoRepository.findAll().get(0);
        var beforeTitle = todo.getTitle();
        var request = new TodoUpdateRequest(
                null,
                LocalDate.of(2025, 1, 1)
        );
        todoService.update(todo.getId(), request);
        var updatedTodo = todoRepository.findById(todo.getId())
                .orElseThrow(() -> new EntityNotFoundException("Todo가 없습니다."));

        assertThat(updatedTodo.getTitle()).isEqualTo(beforeTitle);
        assertThat(updatedTodo.getDeadline()).isEqualTo(LocalDate.of(2025, 1, 1));
    }

    @DisplayName("Todo 수정 시 Todo가 없을 경우 예외 발생")
    @Test
    void updateWithEmptyTodo() {
        var todo = todoRepository.findAll().get(0);
        var request = new TodoUpdateRequest(
                "최종 프로젝트",
                LocalDate.of(2025, 1, 1)
        );
        todoRepository.deleteAll();

        assertThatThrownBy(() -> todoService.updateAll(todo.getId(), request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Todo not found with id: ");

        assertThatThrownBy(() -> todoService.update(todo.getId(), request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Todo not found with id: ");
    }

    @DisplayName("Todo 하나를 삭제할 수 있다.")
    @Test
    void deleteTodo() {
        var todo = todoRepository.findAll().get(0);
        todoService.delete(todo.getId());
        var deletedTodo = todoRepository.findById(todo.getId());

        assertThat(deletedTodo).isEmpty();
    }

    @DisplayName("Todo 삭제 시 Todo가 없을 경우 예외 발생")
    @Test
    void deleteWithEmptyTodo() {

        assertThatThrownBy(() -> todoService.delete(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Todo not found with id: ");
    }
}
