package project.todo.service.todo;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.todo.model.member.Member;
import project.todo.model.todo.Todo;
import project.todo.repository.member.MemberRepository;
import project.todo.repository.todo.TodoRepository;
import project.todo.service.todo.dto.TodoCreateRequest;
import project.todo.service.todo.dto.TodoUpdateRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class TodoWriteServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TodoWriteService todoWriteService;

    @Autowired
    private TodoRepository todoRepository;

    private final LocalDateTime afterDeadline = LocalDateTime.of(
            2025, 1, 2, 23, 59, 59
    );

    @BeforeEach
    void setUp() {
        var member = new Member("user");
        memberRepository.save(member);

        var todos = List.of(
                new Todo(
                        member.getId(),
                        "todo1",
                        LocalDate.of(2025, 1, 1)
                ),
                new Todo(
                        member.getId(),
                        "todo2",
                        LocalDate.of(2025, 1, 1)
                ),
                new Todo(
                        member.getId(),
                        "todo3",
                        LocalDate.of(2025, 1, 1)
                )
        );
        todoRepository.saveAll(todos);
    }

    @DisplayName("사용자는 새로운 Todo를 작성할 수 있다.")
    @Test
    void createTodo() {
        var member = memberRepository.findAll().get(0);
        var deadline = LocalDate.of(2025, 1, 1);
        var newTodo = new TodoCreateRequest(
                "new Todo",
                deadline
        );
        todoWriteService.create(member.getId(), newTodo);

        var todos = todoRepository.findAll();
        assertThat(todos).hasSize(4);
        assertThat(todos.get(3).getTitle()).isEqualTo("new Todo");
        assertThat(todos.get(3).getDeadline()).isEqualTo(deadline.atTime(23, 59, 59));
    }

    @DisplayName("Todo의 제목과 마감일을 수정할 수 있다.")
    @Test
    void updateTitleAndDeadline() {
        var todo = todoRepository.findAll().get(0);
        var request = new TodoUpdateRequest(
                "update Todo",
                LocalDate.of(2025, 1, 2)
        );
        todoWriteService.update(todo.getId(), request);
        var updatedTodo = todoRepository.findById(todo.getId())
                .orElseThrow(() -> new EntityNotFoundException("Todo가 없습니다."));

        assertThat(updatedTodo.getTitle()).isEqualTo("update Todo");
        assertThat(updatedTodo.getDeadline()).isEqualTo(afterDeadline);
    }

    @DisplayName("Todo의 제목만 수정할 수 있다.")
    @Test
    void updateTitle() {
        var todo = todoRepository.findAll().get(0);
        var beforeDeadline = todo.getDeadline();
        var request = new TodoUpdateRequest(
                "update Todo",
                null
        );
        todoWriteService.update(todo.getId(), request);
        var updatedTodo = todoRepository.findById(todo.getId())
                .orElseThrow(() -> new EntityNotFoundException("Todo가 없습니다."));

        assertThat(updatedTodo.getTitle()).isEqualTo("update Todo");
        assertThat(updatedTodo.getDeadline()).isEqualTo(beforeDeadline);
    }

    @DisplayName("Todo의 마감일만 수정할 수 있다.")
    @Test
    void updateDeadline() {
        var todo = todoRepository.findAll().get(0);
        var beforeTitle = todo.getTitle();
        var request = new TodoUpdateRequest(
                null,
                LocalDate.of(2025, 1, 2)
        );
        todoWriteService.update(todo.getId(), request);
        var updatedTodo = todoRepository.findById(todo.getId())
                .orElseThrow(() -> new EntityNotFoundException("Todo가 없습니다."));

        assertThat(updatedTodo.getTitle()).isEqualTo(beforeTitle);
        assertThat(updatedTodo.getDeadline()).isEqualTo(afterDeadline);
    }

    @DisplayName("Todo를 삭제할 수 있다.")
    @Test
    void deleteTodo() {
        var todo = todoRepository.findAll().get(0);
        todoWriteService.delete(todo.getId());
        var deletedTodo = todoRepository.findById(todo.getId());

        assertThat(deletedTodo).isEmpty();
    }
}
