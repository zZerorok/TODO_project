package project.todo.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import project.todo.model.member.Member;
import project.todo.model.member.MemberRepository;
import project.todo.model.todo.Todo;
import project.todo.model.todo.TodoRepository;
import project.todo.model.todo.TodoResponse;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

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

        var todo1 = new Todo(
                member,
                "프로젝트1",
                LocalDate.of(2024, 9, 1)
        );
        todoRepository.save(todo1);

        var todo2 = new Todo(
                member,
                "프로젝트2",
                LocalDate.of(2024, 10, 11)
        );

        todoRepository.save(todo2);

        var todo3 = new Todo(
                member,
                "프로젝트3",
                LocalDate.of(2024, 11, 27)
        );
        todoRepository.save(todo3);
    }

    @DisplayName("사용자의 Todo 전체 조회 기능")
    @Test
    void findAll() {
        Member member = memberRepository.findAll().get(0);

        List<TodoResponse> todos = todoService.findAll(member.getId());

        assertNotNull(todos);
        assertThat(todos.size()).isEqualTo(3);
        assertEquals(todos.get(0).title(), "프로젝트1");
    }

    @DisplayName("작성한 Todo가 없을 경우 예외 발생")
    @Test
    void findAllWithNoTodos() {
        todoRepository.deleteAll();
        Member member = memberRepository.findAll().get(0);

        assertThatThrownBy(() -> {
            todoService.findAll(member.getId());
        })
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("작성하신 Todo가 없습니다.");
    }

    @DisplayName("Todo 삭제 기능")
    @Test
    void delete() {
        var todo = todoRepository.findAll().get(0);

        todoService.delete(todo.getId());

        var deletedTodo = todoRepository.findById(todo.getId())
                .orElse(null);
        assertNull(deletedTodo);
    }

    @DisplayName("존재하지 않는 Todo를 삭제하려고 할 때 예외 발생")
    @Test
    void deleteNonExisting() {
        var nonExistingTodoId = 999L;

        assertThatThrownBy(() -> {
            todoService.delete(nonExistingTodoId);
        })
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Todo not found with id: " + nonExistingTodoId);
    }
}
