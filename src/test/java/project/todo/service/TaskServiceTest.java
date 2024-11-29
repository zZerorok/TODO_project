package project.todo.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.todo.model.member.Member;
import project.todo.model.member.MemberRepository;
import project.todo.model.todo.Todo;
import project.todo.model.todo.TodoRepository;
import project.todo.model.todo.task.Task;
import project.todo.model.todo.task.TaskAddRequest;
import project.todo.model.todo.task.TaskRepository;
import project.todo.model.todo.task.TaskResponse;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @DisplayName("Todo에 포함된 Task 전체 조회")
    @Test
    void findTasksTest() {
        Long todoId = todoRepository.findAll().get(0).getId();

        List<TaskResponse> tasks = taskService.findTasks(todoId);

        assertThat(tasks).hasSize(3);
    }

    @DisplayName("Todo에 포함된 Task 전체 조회 시 Task가 존재하지 않을 경우 예외 발생")
    @Test
    void findNoTaskTest() {
        taskRepository.deleteAll();
        Long todoId = todoRepository.findAll().get(0).getId();

        assertThatThrownBy(() -> {
            taskService.findTasks(todoId);
        })
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 Todo에 대한 Task가 존재하지 않습니다.");
    }

    @DisplayName("Todo에 새로운 Task 추가")
    @Test
    void addTaskTest() {
        Long todoId = todoRepository.findAll().get(0).getId();
        TaskAddRequest task = new TaskAddRequest("task1");
        taskService.add(todoId, task);

        Task addedTask = taskRepository.findAll().stream()
                .filter(it -> it.getContent().equals("task1"))
                .findFirst()
                .orElseThrow();

        assertNotNull(addedTask);
        assertEquals(addedTask.getTodo().getId(), todoId);
        assertEquals(addedTask.getContent(), task.content());
    }

    @DisplayName("Task 완료 처리")
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

    @DisplayName("이미 완료된 Task 에외 발생 ")
    @Test
    void alreadyCompletedTaskTest() {
        Long taskId = taskRepository.findAll().get(0).getId();
        taskService.completeTask(taskId);

        assertThatThrownBy(() -> {
            taskService.completeTask(taskId);
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 완료된 Task입니다.");
    }

    @DisplayName("Task 삭제 처리")
    @Test
    void deleteTaskTest() {
        Long taskId = taskRepository.findAll().get(0).getId();

        taskService.delete(taskId);

        assertThat(taskRepository.findById(taskId)).isEmpty();
        assertThat(taskRepository.findAll()).hasSize(2);
    }

    @DisplayName("Task 삭제 시 Task가 존재하지 않으면 예외 발생")
    @Test
    void deleteNoTaskTest() {
        Long invalidTaskId = 999L;

        assertThatThrownBy(() -> {
            taskService.delete(invalidTaskId);
        })
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("해당 Task를 찾을 수 없습니다.");
    }
}
