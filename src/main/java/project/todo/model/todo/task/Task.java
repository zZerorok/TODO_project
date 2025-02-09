package project.todo.model.todo.task;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.todo.exception.todo.DeadlineExceededException;
import project.todo.exception.todo.task.TaskNotInTodoException;
import project.todo.exception.todo.task.TaskStateException;
import project.todo.model.todo.Status;
import project.todo.model.todo.Todo;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "todo_id")
    private Todo todo;
    private String content;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Task(Todo todo, String content) {
        this(todo, content, LocalDateTime.now());
    }

    public Task(Todo todo, String content, LocalDateTime createdAt) {
        validateForCreate(todo, content, createdAt);

        this.id = 0L;
        this.todo = todo;
        this.content = content;
        this.createdAt = createdAt;
        this.status = Status.INCOMPLETE;
    }

    private void validateForCreate(Todo todo, String content, LocalDateTime createdAt) {
        validateContent(content);
        validateDeadline(todo, createdAt);
    }

    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용은 공백일 수 없습니다.");
        }
    }

    private void validateDeadline(Todo todo, LocalDateTime createdAt) {
        if (todo.getDeadline().isBefore(createdAt)) {
            throw new DeadlineExceededException("마감일이 초과되어 Task를 생성할 수 없습니다.");
        }
    }

    public void update(String content) {
        validateForUpdate(content);

        this.content = content;
    }

    private void validateForUpdate(String content) {
        validateContent(content);

        if (this.status.isCompleted()) {
            throw new TaskStateException("이미 완료된 Task는 수정할 수 없습니다.");
        }

        if (this.todo.getDeadline().isBefore(LocalDateTime.now())) {
            throw new DeadlineExceededException("마감일이 초과되어 수정할 수 없습니다.");
        }
    }

    public void complete() {
        checkDeadline(Status.COMPLETE);

        if (this.status.isCompleted()) {
            throw new TaskStateException("이미 완료된 Task는 완료 처리할 수 없습니다.");
        }

        this.status = Status.COMPLETE;
    }

    public void incomplete() {
        checkDeadline(Status.INCOMPLETE);

        if (!this.status.isCompleted()) {
            throw new TaskStateException("완료되지 않은 Task는 완료 해제할 수 없습니다.");
        }

        this.status = Status.INCOMPLETE;
    }

    private void checkDeadline(Status status) {
        todo.validateForUpdateStatus(status);
    }

    public void validateWriter(long memberId) {
        todo.validateWriter(memberId);
    }

    public void validateTodo(long todoId) {
        if (!this.todo.getId().equals(todoId)) {
            throw new TaskNotInTodoException("해당 Task는 " + todoId + "에 포함되어 있지 않습니다.");
        }
    }
}
