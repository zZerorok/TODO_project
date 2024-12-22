package project.todo.service.todo.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.todo.model.todo.Todo;
import project.todo.model.todo.TodoStatus;
import project.todo.model.todo.task.Task;
import project.todo.model.todo.task.TaskStatus;
import project.todo.repository.todo.TodoRepository;
import project.todo.repository.todo.task.TaskRepository;
import project.todo.service.todo.task.dto.TaskAddRequest;
import project.todo.service.todo.task.dto.TaskUpdateRequest;

import java.time.LocalDate;
import java.util.List;

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

        var tasks = List.of(
                new Task(
                        todo,
                        "task1"
                ),
                new Task(
                        todo,
                        "task2"
                )
        );
        taskRepository.saveAll(tasks);
    }

    @DisplayName("Task를 추가할 수 있다.")
    @Test
    void addTask() {
        var todo = todoRepository.findAll().get(0);
        var request = new TaskAddRequest("new Task");

        taskWriteService.add(todo.getId(), request);

        var tasks = taskRepository.findAll();
        assertThat(tasks).hasSize(3);
    }

    @DisplayName("Task를 수정할 수 있다.")
    @Test
    void updateTask() {
        var task = taskRepository.findAll().get(0);
        var request = new TaskUpdateRequest("update Task");

        taskWriteService.update(task.getId(), request);

        var updatedTask = taskRepository.findById(task.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        assertThat(updatedTask.getId()).isEqualTo(task.getId());
        assertThat(updatedTask.getContent()).isEqualTo(request.content());
    }

    @DisplayName("Task 하나를 완료 처리할 수 있다.")
    @Test
    void completeTask() {
        var todo = todoRepository.findAll().get(0);
        var task = taskRepository.findAll().get(0);

        taskWriteService.complete(todo.getId(), task.getId());

        var completedTask = taskRepository.findById(task.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        assertThat(completedTask.getStatus()).isEqualTo(TaskStatus.COMPLETED);
    }

    @DisplayName("Task가 전부 완료되면 Todo도 완료 처리된다.")
    @Test
    void completeAllTasks() {
        var todo = todoRepository.findAll().get(0);
        var tasks = taskRepository.findAll();

        for (Task task : tasks) {
            taskWriteService.complete(todo.getId(), task.getId());
        }

        var completedTodo = todoRepository.findById(todo.getId())
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        assertThat(completedTodo.getStatus()).isEqualTo(TodoStatus.COMPLETED);
    }

    @DisplayName("모든 Task가 완료 되지 않으면 Todo도 완료 처리 되지 않는다.")
    @Test
    void todoWithNotAllTasksComplete() {
        var todo = todoRepository.findAll().get(0);
        var task = taskRepository.findAll().get(0);

        taskWriteService.complete(todo.getId(), task.getId());

        var completedTodo = todoRepository.findById(todo.getId())
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        assertThat(completedTodo.getStatus()).isEqualTo(TodoStatus.INCOMPLETE);
    }

    @DisplayName("Task를 미완료 처리할 수 있다.")
    @Test
    void incompleteTask() {
        var todo = todoRepository.findAll().get(0);
        var task = taskRepository.findAll().get(0);
        taskWriteService.complete(todo.getId(), task.getId());

        var completedTask = taskRepository.findById(task.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskWriteService.incomplete(todo.getId(), completedTask.getId());

        var incompletedTask = taskRepository.findById(completedTask.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));
        assertThat(incompletedTask.getStatus()).isEqualTo(TaskStatus.INCOMPLETE);
    }

    @DisplayName("Task 하나라도 미완료 처리하면 Todo도 미완료 처리된다.")
    @Test
    void todoWithOnceIncomplete() {
        var todo = todoRepository.findAll().get(0);
        var tasks = taskRepository.findAll();
        for (Task task : tasks) {
            taskWriteService.complete(todo.getId(), task.getId());
        }

        var completedTask = taskRepository.findAll().get(0);
        taskWriteService.incomplete(todo.getId(), completedTask.getId());

        var incompletedTodo = todoRepository.findById(todo.getId())
                .orElseThrow(() -> new RuntimeException("Todo not found"));
        assertThat(incompletedTodo.getStatus()).isEqualTo(TodoStatus.INCOMPLETE);
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
