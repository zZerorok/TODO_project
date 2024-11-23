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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNull;

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

        var todo = new Todo(
                member,
                "프로젝트",
                LocalDate.of(2024, 11, 11)
        );
        todoRepository.save(todo);
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
