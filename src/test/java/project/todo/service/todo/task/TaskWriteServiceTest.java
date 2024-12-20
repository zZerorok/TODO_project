package project.todo.service.todo.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.todo.model.todo.Todo;
import project.todo.model.todo.task.Task;
import project.todo.model.todo.task.TaskStatus;
import project.todo.repository.todo.TodoRepository;
import project.todo.repository.todo.task.TaskRepository;
import project.todo.service.todo.task.dto.TaskAddRequest;
import project.todo.service.todo.task.dto.TaskUpdateRequest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class TaskWriteServiceTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskWriteService taskWriteService;

    @BeforeEach
    void setUp() {
        var todo = new Todo(
                1L,
                "todo",
                LocalDate.of(2025, 12, 1)
        );
        todoRepository.save(todo);

        var task = new Task(
                todo,
                "new Task"
        );
        taskRepository.save(task);
    }

    @DisplayName("Task를 추가할 수 있다.")
    @Test
    void addTask() {
        var todo = todoRepository.findAll().get(0);
        var request1 = new TaskAddRequest("new Task1");
        var request2 = new TaskAddRequest("new Task2");

        taskWriteService.add(todo.getId(), request1);
        taskWriteService.add(todo.getId(), request2);

        var tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(3);
    }

    @DisplayName("Task를 수정할 수 있다.")
    @Test
    void updateTask() {
        var task = taskRepository.findAll().get(0);
        assertThat(task.getContent()).isEqualTo("new Task");

        taskWriteService.update(
                task.getId(),
                new TaskUpdateRequest("update Task")
        );
        var updatedTask = taskRepository.findById(task.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        assertThat(updatedTask.getId()).isEqualTo(task.getId());
        assertThat(updatedTask.getContent()).isEqualTo("update Task");
    }

    @DisplayName("Task를 완료 처리할 수 있다.")
    @Test
    void completeTask() {
        var todo = todoRepository.findAll().get(0);
        var task = taskRepository.findAll().get(0);
        assertThat(task.getStatus()).isEqualTo(TaskStatus.INCOMPLETE);

        taskWriteService.complete(todo.getId(), task.getId());
        var completedTask = taskRepository.findById(task.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        assertThat(completedTask.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    }

    @DisplayName("Task를 미완료 처리할 수 있다.")
    @Test
    void incompleteTask() {
        var todo = todoRepository.findAll().get(0);
        var task = taskRepository.findAll().get(0);
        taskWriteService.complete(todo.getId(), task.getId());
        var completedTask = taskRepository.findById(task.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        assertThat(completedTask.getStatus()).isEqualTo(TaskStatus.COMPLETED);

        taskWriteService.incomplete(todo.getId(), completedTask.getId());
        var incompletedTask = taskRepository.findById(completedTask.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        assertThat(incompletedTask.getStatus()).isEqualTo(TaskStatus.INCOMPLETE);
    }

    @DisplayName("Task를 삭제할 수 있다.")
    @Test
    void deleteTask() {
        var task = taskRepository.findAll().get(0);
        taskWriteService.delete(task.getId());
        var deletedTask = taskRepository.findById(task.getId());

        assertThat(deletedTask).isEmpty();
    }
}
