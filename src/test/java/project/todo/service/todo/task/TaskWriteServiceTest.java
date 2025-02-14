package project.todo.service.todo.task;

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
import project.todo.service.todo.task.dto.TaskAddRequest;
import project.todo.service.todo.task.dto.TaskUpdateRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class TaskWriteServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskWriteService taskWriteService;

    public static final Status COMPLETE_STATUS = Status.COMPLETE;
    public static final Status INCOMPLETE_STATUS = Status.INCOMPLETE;
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

        var todo = new Todo(
                0L,
                "todo",
                LocalDate.of(2030, 1, 1)
        );
        todoRepository.save(todo);

        var savedTodo = todoRepository.findAll().get(0);
        var tasks = List.of(
                new Task(savedTodo, "task1"),
                new Task(savedTodo, "task2")
        );
        taskRepository.saveAll(tasks);
    }

    @DisplayName("Todo에 새로운 Task를 추가할 수 있다.")
    @Test
    void addTask() {
        var todo = todoRepository.findAll().get(0);
        var request = new TaskAddRequest("new Task");

        taskWriteService.add(LOGIN_MEMBER, todo.getId(), request);

        var tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(3);
        assertThat(tasks.get(2).getContent()).isEqualTo("new Task");
    }

    @DisplayName("Task 내용을 수정할 수 있다.")
    @Test
    void updateTask() {
        var todo = todoRepository.findAll().get(0);
        var task = taskRepository.findAll().get(0);
        var request = new TaskUpdateRequest("update Task");

        taskWriteService.update(LOGIN_MEMBER, todo.getId(), task.getId(), request);

        assertThat(task.getContent()).isEqualTo("update Task");
    }

    @DisplayName("Task를 완료 처리할 수 있다.")
    @Test
    void completeTask() {
        var todo = todoRepository.findAll().get(0);
        var task = taskRepository.findAll().get(0);

        taskWriteService.updateStatus(LOGIN_MEMBER, todo.getId(), task.getId(), COMPLETE_STATUS);

        assertThat(task.getStatus()).isEqualTo(COMPLETE_STATUS);
    }

    @DisplayName("Task가 전부 완료되면 Todo도 완료 처리된다.")
    @Test
    void completeAllTasks() {
        var todo = todoRepository.findAll().get(0);
        var tasks = taskRepository.findAll();

        for (Task task : tasks) {
            taskWriteService.updateStatus(LOGIN_MEMBER, todo.getId(), task.getId(), COMPLETE_STATUS);
        }

        assertThat(todo.getStatus()).isEqualTo(COMPLETE_STATUS);
    }

    @DisplayName("Task를 미완료 처리할 수 있다.")
    @Test
    void incompleteTask() {
        var todo = todoRepository.findAll().get(0);
        var task = taskRepository.findAll().get(0);
        taskWriteService.updateStatus(LOGIN_MEMBER, todo.getId(), task.getId(), COMPLETE_STATUS);

        taskWriteService.updateStatus(LOGIN_MEMBER, todo.getId(), task.getId(), INCOMPLETE_STATUS);

        assertThat(task.getStatus()).isEqualTo(INCOMPLETE_STATUS);
    }

    @DisplayName("Task 하나라도 미완료 처리하면 Todo도 미완료 처리된다.")
    @Test
    void todoWithOnceIncomplete() {
        var todo = todoRepository.findAll().get(0);
        var tasks = taskRepository.findAll();
        for (Task task : tasks) {
            taskWriteService.updateStatus(LOGIN_MEMBER, todo.getId(), task.getId(), COMPLETE_STATUS);
        }

        taskWriteService.updateStatus(LOGIN_MEMBER, todo.getId(), tasks.get(0).getId(), INCOMPLETE_STATUS);

        assertThat(todo.getStatus()).isEqualTo(INCOMPLETE_STATUS);
    }

    @DisplayName("Task를 삭제할 수 있다.")
    @Test
    void deleteTask() {
        var todo = todoRepository.findAll().get(0);
        var task = taskRepository.findAll().get(0);

        taskWriteService.delete(LOGIN_MEMBER, todo.getId(), task.getId());

        var deletedTask = taskRepository.findById(task.getId());
        assertThat(deletedTask).isEmpty();
    }

    @DisplayName("마지막 Task를 삭제하면 Todo 상태가 미완료로 변경된다")
    @Test
    void deleteLastTaskAfterUpdateTodoStatus() {
        var todo = todoRepository.findAll().get(0);
        var tasks = taskRepository.findAll();
        for (Task task : tasks) {
            taskWriteService.updateStatus(LOGIN_MEMBER, todo.getId(), task.getId(), COMPLETE_STATUS);
        }

        for (Task task : tasks) {
            taskWriteService.delete(LOGIN_MEMBER, todo.getId(), task.getId());
        }

        assertThat(todo.getStatus()).isEqualTo(INCOMPLETE_STATUS);
    }

    @DisplayName("Task가 여러 개 있을 경우 하나만 삭제해도 Todo 상태는 유지된다.")
    @Test
    void deleteOneTaskWhenMultipleExist() {
        var todo = todoRepository.findAll().get(0);
        var tasks = taskRepository.findAll();
        for (Task task : tasks) {
            taskWriteService.updateStatus(LOGIN_MEMBER, todo.getId(), task.getId(), COMPLETE_STATUS);
        }

        taskWriteService.delete(LOGIN_MEMBER, todo.getId(), tasks.get(0).getId());

        assertThat(todo.getStatus()).isEqualTo(COMPLETE_STATUS);
    }
}
