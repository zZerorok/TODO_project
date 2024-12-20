package project.todo.service.todo.task;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.todo.model.todo.Todo;
import project.todo.model.todo.task.Task;
import project.todo.repository.todo.TodoRepository;
import project.todo.repository.todo.task.TaskRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Transactional
@SpringBootTest
class TaskReadServiceTest {

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TaskReadService taskReadService;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        var todo = new Todo(
                1L,
                "todo",
                LocalDate.of(2025, 12, 1)
        );
        todoRepository.save(todo);
    }

    @DisplayName("Todo 번호에 해당하는 Task 전체 조회를 할 수 있다.")
    @Test
    void findTasks() {
        var todo = todoRepository.findAll().get(0);
        var tasks = List.of(
                new Task(todo, "task1"),
                new Task(todo, "task2"),
                new Task(todo, "task3")
        );
        taskRepository.saveAll(tasks);
        var getTasks = taskReadService.findTasks(todo.getId());

        assertThat(getTasks).hasSize(3);
        assertThat(getTasks.get(0).content()).isEqualTo("task1");
        assertThat(getTasks.get(1).content()).isEqualTo("task2");
        assertThat(getTasks.get(2).content()).isEqualTo("task3");
    }

    @DisplayName("Task 전체 조회 시 Task가 없는 경우 빈 리스트를 반환한다.")
    @Test
    void findTasksWithEmptyTask() {
        var todoId = todoRepository.findAll().get(0).getId();
        var tasks = taskReadService.findTasks(todoId);

        assertThat(tasks).isEmpty();
    }

    @DisplayName("Task 전체 조회 시 잘못된 Todo 번호면 예외발생")
    @Test
    void findTasksWithWrongTodo() {

        assertThatThrownBy(() -> taskReadService.findTasks(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 Todo를 찾을 수 없습니다.");
    }
}
