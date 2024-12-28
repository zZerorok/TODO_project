package project.todo.service.todo;

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

    @DisplayName("사용자가 작성한 Todo 중 완료된 Todo만 조회할 수 있다.")
    @Test
    void findCompleteTodos() {
        var todo1 = todoRepository.findAll().get(0);
        todo1.complete();
        todoRepository.save(todo1);

        var completeTodos = todoReadService.findCompleteTodos(todo1.getMemberId());

        assertThat(completeTodos).hasSize(1);
        assertThat(completeTodos.get(0).status().isCompleted()).isTrue();
    }

    @DisplayName("완료된 Todo 조회 시 완료된 Todo가 없으면 빈 리스트를 반환한다.")
    @Test
    void findCompleteTodosWithEmpty() {
        var member = memberRepository.findAll().get(0);

        var completeTodos = todoReadService.findCompleteTodos(member.getId());

        assertThat(completeTodos).isEmpty();
    }

    @DisplayName("사용자가 작성한 Todo 중 미완료된 Todo만 조회할 수 있다.")
    @Test
    void findIncompleteTodos() {
        var todo1 = todoRepository.findAll().get(0);
        todo1.complete();
        todoRepository.save(todo1);

        var incompleteTodos = todoReadService.findIncompleteTodos(todo1.getMemberId());

        assertThat(incompleteTodos).hasSize(2);
        assertThat(incompleteTodos.get(0).status().isCompleted()).isFalse();
    }

    @DisplayName("미완료된 Todo 조회 시 미완료된 Todo가 없으면 빈 리스트를 반환한다.")
    @Test
    void findIncompleteTodosWithEmpty() {
        var todos = todoRepository.findAll();
        todos.forEach(Todo::complete);
        todoRepository.saveAll(todos);
        var member = memberRepository.findAll().get(0);

        var incompleteTodos = todoReadService.findIncompleteTodos(member.getId());

        assertThat(incompleteTodos).isEmpty();
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
