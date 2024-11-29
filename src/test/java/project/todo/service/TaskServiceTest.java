package project.todo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.todo.model.member.Member;
import project.todo.model.member.MemberRepository;
import project.todo.model.todo.Todo;
import project.todo.model.todo.TodoRepository;
import project.todo.model.todo.task.Task;
import project.todo.model.todo.task.TaskRepository;
import project.todo.model.todo.task.TaskResponse;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class TaskServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        Member member = new Member("사용자");
        memberRepository.save(member);

        Todo todo = new Todo(
                member,
                "프로젝트",
                LocalDate.of(2024, 12, 1)
        );
        todoRepository.save(todo);

        List<Task> tasks = List.of(
                new Task(todo, "작업1"),
                new Task(todo, "작업2"),
                new Task(todo ,"작업3")
        );
        taskRepository.saveAll(tasks);
    }

    @Test
    void addTaskTest() {
        Todo todo = todoRepository.findAll().get(0);
        Task task = new Task(
                todo,
                "task1"
        );
        Task savedTask = taskRepository.save(task);

        assertThat(savedTask).isNotNull();
        assertThat(savedTask.getContent()).isEqualTo("task1");
    }

    @Test
    void findTasksTest() {
        Todo todo = todoRepository.findAll().get(0);

        List<TaskResponse> tasks = taskService.findTasks(todo.getId());

        assertThat(tasks).isNotNull();
        assertThat(tasks).hasSize(3);
    }

    @Test
    void deleteTaskTest() {
        Task task = taskRepository.findAll().get(0);
        Long taskId = task.getId();

        taskService.delete(taskId);

        assertThat(taskRepository.findById(taskId)).isEmpty();
        assertThat(taskRepository.findAll()).hasSize(2);
    }

    @Test
    void completeTaskTest() {
        Task task = taskRepository.findAll().get(0);
        Long taskId = task.getId();
        assertNull(task.getCompletedAt());
        assertFalse(task.isCompleted());

        taskService.completeTask(taskId);

        Task completedTask = taskRepository.findById(taskId).orElseThrow();
        assertNotNull(completedTask.getCompletedAt());
        assertTrue(completedTask.isCompleted());
    }
}
