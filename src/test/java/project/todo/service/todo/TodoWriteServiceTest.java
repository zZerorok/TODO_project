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
import project.todo.service.todo.dto.TodoCreateRequest;
import project.todo.service.todo.dto.TodoUpdateRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class TodoWriteServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TodoWriteService todoWriteService;

    private static final LoginMember LOGIN_MEMBER = new LoginMember(0L, "사용자");

    private final LocalDateTime deadline = LocalDate.of(2030, 1, 2)
            .atTime(LocalTime.MAX);

    @BeforeEach
    void setUp() {
        var member = new Member(
                "사용자",
                "loginId",
                "password123",
                "test@example.com"
        );
        memberRepository.save(member);

        var todo = new Todo(
                0L,
                "todo",
                LocalDate.of(2030, 1, 1)
        );
        todoRepository.save(todo);
    }

    @DisplayName("새로운 Todo를 작성할 수 있다.")
    @Test
    void createTodo() {
        var request = new TodoCreateRequest(
                "new Todo",
                deadline.toLocalDate()
        );

        todoWriteService.create(LOGIN_MEMBER, request);

        var todos = todoRepository.findAll();
        assertThat(todos).hasSize(2);
        assertThat(todos.get(1).getTitle()).isEqualTo("new Todo");
        assertThat(todos.get(1).getDeadline()).isEqualTo(deadline);
    }

    @DisplayName("Todo의 제목과 마감일을 수정할 수 있다.")
    @Test
    void updateTitleAndDeadline() {
        var todo = todoRepository.findAll().get(0);
        var request = new TodoUpdateRequest(
                "update Todo",
                deadline.toLocalDate()
        );

        todoWriteService.update(LOGIN_MEMBER, todo.getId(), request);

        assertThat(todo.getTitle()).isEqualTo("update Todo");
        assertThat(todo.getDeadline()).isEqualTo(deadline);

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

        todoWriteService.update(LOGIN_MEMBER, todo.getId(), request);

        assertThat(todo.getTitle()).isEqualTo("update Todo");
        assertThat(todo.getDeadline()).isEqualTo(beforeDeadline);
    }

    @DisplayName("Todo의 마감일만 수정할 수 있다.")
    @Test
    void updateDeadline() {
        var todo = todoRepository.findAll().get(0);
        var beforeTitle = todo.getTitle();
        var request = new TodoUpdateRequest(
                null,
                deadline.toLocalDate()
        );

        todoWriteService.update(LOGIN_MEMBER, todo.getId(), request);

        assertThat(todo.getTitle()).isEqualTo(beforeTitle);
        assertThat(todo.getDeadline()).isEqualTo(deadline);
    }

    @DisplayName("Task가 전부 완료된 경우에 Todo를 완료 처리할 수 있다.")
    @Test
    void completeTodo() {
        var todo = todoRepository.findAll().get(0);
        var task = taskRepository.save(new Task(todo, "task", LocalDateTime.now()));
        task.complete();

        todoWriteService.updateStatus(LOGIN_MEMBER, todo.getId(), Status.COMPLETE);

        assertThat(todo.getStatus()).isEqualTo(Status.COMPLETE);
    }

    @DisplayName("Task가 하나라도 미완료인 경우 Todo를 완료 처리할 수 없다.")
    @Test
    void completeTodoWithIncompleteTask() {
        var todo = todoRepository.findAll().get(0);
        taskRepository.save(new Task(todo, "task", LocalDateTime.now()));

        todoWriteService.updateStatus(LOGIN_MEMBER, todo.getId(), Status.COMPLETE);

        assertThat(todo.getStatus()).isEqualTo(Status.INCOMPLETE);
    }

    @DisplayName("Todo를 미완료 처리할 수 있다.")
    @Test
    void incompleteTodo() {
        var todo = todoRepository.findAll().get(0);
        todo.complete();

        todoWriteService.updateStatus(LOGIN_MEMBER, todo.getId(), Status.INCOMPLETE);

        assertThat(todo.getStatus()).isEqualTo(Status.INCOMPLETE);
    }

    @DisplayName("Todo를 삭제할 수 있다.")
    @Test
    void deleteTodo() {
        var todo = todoRepository.findAll().get(0);

        todoWriteService.delete(LOGIN_MEMBER, todo.getId());

        var deletedTodo = todoRepository.findById(todo.getId());
        assertThat(deletedTodo).isEmpty();
    }
}
