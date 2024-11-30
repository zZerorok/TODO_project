package project.todo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.todo.exception.todo.DeadlineExceededException;
import project.todo.model.member.Member;
import project.todo.model.todo.Todo;
import project.todo.model.todo.task.Task;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskTest {

    @DisplayName("Task 생성")
    @Test
    void taskTest() {
        LocalDate deadline = LocalDate.of(2025, 12, 30);
        Todo todo = new Todo(
                new Member("사용자"),
                "todo",
                deadline
        );
        Task task = new Task(todo, "task");

        assertThat(task.getContent()).isEqualTo("task");
        assertThat(task.getTodo()).isEqualTo(todo);
    }

    @DisplayName("Task 생성 시 마감일을 초과한 경우 예외 발생")
    @Test
    void deadlineTest() {
        LocalDate deadline = LocalDate.of(2024, 11, 11);
        Todo todo = new Todo(
                new Member("사용자"),
                "todo",
                deadline
        );

        assertThatThrownBy(() -> {
            new Task(todo, "task");
        })
                .isInstanceOf(DeadlineExceededException.class)
                .hasMessage("마감일(%s)을 초과할 수 없습니다.", deadline);
    }

    @DisplayName("Task 완료")
    @Test
    void completeTaskTest() {
        LocalDate deadline = LocalDate.of(2025, 12, 30);
        Todo todo = new Todo(
                new Member("사용자"),
                "todo",
                deadline
        );
        Task task = new Task(todo, "task");
        assertNull(task.getCompletedAt());
        assertFalse(task.isCompleted());

        task.completeTask();
        assertNotNull(task.getCompletedAt());
        assertTrue(task.isCompleted());
    }

    @DisplayName("이미 완료된 Task 에외 발생 ")
    @Test
    void alreadyCompletedTaskTest() {
        LocalDate deadline = LocalDate.of(2025, 12, 30);
        Todo todo = new Todo(
                new Member("사용자"),
                "todo",
                deadline
        );
        Task task = new Task(todo, "task");
        task.completeTask();

        assertThatThrownBy(task::completeTask)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 완료된 Task 입니다.");
    }
}
