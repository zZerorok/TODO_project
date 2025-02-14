package project.todo.service.todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.todo.model.member.Member;
import project.todo.model.todo.Status;
import project.todo.model.todo.Todo;
import project.todo.model.todo.task.Task;
import project.todo.repository.member.MemberRepository;
import project.todo.repository.todo.TodoRepository;
import project.todo.repository.todo.task.TaskRepository;
import project.todo.service.security.dto.LoginMember;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    private static final LoginMember LOGIN_MEMBER = new LoginMember(0L, "사용자");

    @BeforeEach
    void setUp() {
        var member = new Member(
                "사용자",
                "loginId",
                "password123",
                "test@example.com"
        );
        memberRepository.save(member);

        var todos = List.of(
                new Todo(
                        0L,
                        "todo1",
                        LocalDate.of(2030, 9, 1)
                ),
                new Todo(
                        0L,
                        "todo2",
                        LocalDate.of(2030, 10, 11)
                ),
                new Todo(
                        0L,
                        "todo3",
                        LocalDate.of(2030, 11, 27)
                )
        );
        todoRepository.saveAll(todos);

        var savedFirstTodo = todoRepository.findAll().get(0);
        var tasks = List.of(
                new Task(savedFirstTodo, "task1"),
                new Task(savedFirstTodo, "task2"),
                new Task(savedFirstTodo, "task3")
        );
        taskRepository.saveAll(tasks);
    }

    @DisplayName("사용자가 작성한 전체 Todo를 조회할 수 있다.")
    @Test
    void findTodos() {
        var todo = todoRepository.findAll().get(0);
        todo.complete();
        Optional<Status> emptyStatus = Optional.empty();

        var todos = todoReadService.findTodos(LOGIN_MEMBER, emptyStatus);

        assertThat(todos).hasSize(3);
    }

    @DisplayName("사용자가 작성한 Todo 중 완료된 Todo만 조회할 수 있다.")
    @Test
    void findCompleteTodos() {
        var todo = todoRepository.findAll().get(0);
        todo.complete();
        Optional<Status> completeStatus = Optional.of(Status.COMPLETE);

        var todos = todoReadService.findTodos(LOGIN_MEMBER, completeStatus);

        assertThat(todos).hasSize(1);
        assertThat(todos.get(0).status()).isEqualTo(Status.COMPLETE);
    }

    @DisplayName("완료된 Todo 조회 시 완료된 Todo가 없으면 빈 리스트를 반환한다.")
    @Test
    void findCompleteTodosWithEmpty() {
        Optional<Status> completeStatus = Optional.of(Status.COMPLETE);

        var completeTodos = todoReadService.findTodos(LOGIN_MEMBER, completeStatus);

        assertThat(completeTodos).isEmpty();
    }

    @DisplayName("사용자가 작성한 Todo 중 미완료된 Todo만 조회할 수 있다.")
    @Test
    void findIncompleteTodos() {
        Optional<Status> incompleteStatus = Optional.of(Status.INCOMPLETE);

        var todos = todoReadService.findTodos(LOGIN_MEMBER, incompleteStatus);

        assertThat(todos).hasSize(3);
    }

    @DisplayName("미완료된 Todo 조회 시 미완료된 Todo가 없으면 빈 리스트를 반환한다.")
    @Test
    void findIncompleteTodosWithEmpty() {
        var todos = todoRepository.findAll();
        todos.forEach(Todo::complete);
        Optional<Status> incompleteStatus = Optional.of(Status.INCOMPLETE);

        var incompleteTodos = todoReadService.findTodos(LOGIN_MEMBER, incompleteStatus);

        assertThat(incompleteTodos).isEmpty();
    }

    @DisplayName("Todo를 상세 조회 하면 포함된 Task도 출력된다.")
    @Test
    void getTodoDetail() {
        var todo = todoRepository.findAll().get(0);
        var getTodoWithTasks = todoReadService.getTodoWithTasks(LOGIN_MEMBER, todo.getId());

        assertThat(getTodoWithTasks.todoId()).isEqualTo(todo.getId());
        assertThat(getTodoWithTasks.tasks()).hasSize(3);
    }
}
