package project.todo.service.todo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.todo.model.member.Member;
import project.todo.model.todo.Todo;
import project.todo.model.todo.TodoStatus;
import project.todo.model.todo.task.Task;
import project.todo.repository.member.MemberRepository;
import project.todo.repository.todo.TodoRepository;
import project.todo.repository.todo.task.TaskRepository;
import project.todo.service.todo.dto.TodoCreateRequest;
import project.todo.service.todo.dto.TodoUpdateRequest;
import project.todo.service.todo.task.TaskWriteService;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Autowired
    private TaskWriteService taskWriteService;

    private final LocalDateTime deadline = LocalDateTime.of(
            2025, 1, 2, 23, 59, 59
    );

    @BeforeEach
    void setUp() {
        var member = new Member("user");
        memberRepository.save(member);

        var todo = new Todo(
                member.getId(),
                "todo",
                LocalDate.of(2025, 1, 1)
        );
        todoRepository.save(todo);

        var task = new Task(
                todo,
                "task"
        );
        taskRepository.save(task);
    }

    @DisplayName("사용자는 새로운 Todo를 작성할 수 있다.")
    @Test
    void createTodo() {
        var member = memberRepository.findAll().get(0);
        var request = new TodoCreateRequest(
                "new Todo",
                deadline.toLocalDate()
        );

        todoWriteService.create(member.getId(), request);

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

        todoWriteService.update(todo.getId(), request);

        var updatedTodo = todoRepository.findById(todo.getId())
                .orElseThrow(() -> new RuntimeException("todo not found"));
        assertThat(updatedTodo.getTitle()).isEqualTo("update Todo");
        assertThat(updatedTodo.getDeadline()).isEqualTo(deadline);
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
                .orElseThrow(() -> new RuntimeException("Todo not found"));
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
                deadline.toLocalDate()
        );

        todoWriteService.update(todo.getId(), request);

        var updatedTodo = todoRepository.findById(todo.getId())
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        assertThat(updatedTodo.getTitle()).isEqualTo(beforeTitle);
        assertThat(updatedTodo.getDeadline()).isEqualTo(deadline);
    }

    @DisplayName("Task가 전부 완료된 경우 Todo를 완료할 수 있다.")
    @Test
    void completeTodo() {
        var todo = todoRepository.findAll().get(0);
        var task = taskRepository.findAll().get(0);
        taskWriteService.complete(todo.getId(), task.getId());
        todoWriteService.incomplete(todo.getId());

        todoWriteService.complete(todo.getId());

        var completedTodo = todoRepository.findById(todo.getId())
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        assertThat(completedTodo.getStatus()).isEqualTo(TodoStatus.COMPLETED);
    }

    @DisplayName("Todo를 미완료 처리할 수 있다.")
    @Test
    void incompleteTodo() {
        var todo = todoRepository.findAll().get(0);
        var task = taskRepository.findAllByTodoId(todo.getId()).get(0);
        taskWriteService.complete(todo.getId(), task.getId());

        todoWriteService.incomplete(todo.getId());

        var incompletedTodo = todoRepository.findById(todo.getId())
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        assertThat(incompletedTodo.getStatus()).isEqualTo(TodoStatus.INCOMPLETE);
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
