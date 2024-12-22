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
import project.todo.model.todo.task.Task;
import project.todo.repository.member.MemberRepository;
import project.todo.repository.todo.TodoRepository;
import project.todo.repository.todo.task.TaskRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class TodoReadServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TodoReadService todoReadService;

    @BeforeEach
    void setUp() {
        var member = new Member("user");
        memberRepository.save(member);

        var todos = List.of(
                new Todo(
                        member.getId(),
                        "todo1",
                        LocalDate.of(2025, 9, 1)
                ),
                new Todo(
                        member.getId(),
                        "todo2",
                        LocalDate.of(2025, 10, 11)
                ),
                new Todo(
                        member.getId(),
                        "todo3",
                        LocalDate.of(2025, 11, 27)
                )
        );
        todoRepository.saveAll(todos);
    }

    @DisplayName("사용자가 작성한 전체 Todo를 조회할 수 있다.")
    @Test
    void findTodos() {
        var member = memberRepository.findAll().get(0);

        var todos = todoReadService.findTodos(member.getId());

        assertThat(todos).hasSize(3);
    }

    @DisplayName("Todo 조회 시 Todo가 없을 경우 예외 발생")
    @Test
    void findWithEmptyTodos() {
        var member = memberRepository.findAll().get(0);
        todoRepository.deleteAll();

        assertThatThrownBy(() -> todoReadService.findTodos(member.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("작성하신 Todo가 없습니다.");
    }

    @DisplayName("Todo를 상세 조회 하면 포함된 Task도 출력된다.")
    @Test
    void getTodoDetail() {
        var todo = todoRepository.findAll().get(0);
        taskRepository.saveAll(
                List.of(
                        new Task(todo, "task1"),
                        new Task(todo, "task2"),
                        new Task(todo, "task3")
                )
        );

        var getTodoWithTasks = todoReadService.getTodoWithTasks(todo.getId());

        assertThat(getTodoWithTasks.todoId()).isEqualTo(todo.getId());
        assertThat(getTodoWithTasks.tasks()).hasSize(3);
    }
}
