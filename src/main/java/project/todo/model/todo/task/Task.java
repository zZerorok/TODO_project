package project.todo.model.todo.task;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.todo.exception.todo.DeadlineExceededException;
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
    private TaskStatus status;
    private LocalDateTime completedAt;

    public Task(Todo todo, String content) {
        this(todo, content, LocalDateTime.now());
    }

    public Task(Todo todo, String content, LocalDateTime createdAt) {
        validateContent(content);
        validateDeadline(todo, createdAt);

        this.todo = todo;
        this.content = content;
        this.createdAt = createdAt;
        this.status = TaskStatus.INCOMPLETE;
    }

    private void validateContent(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("내용은 공백일 수 없습니다.");
        }
    }

    private void validateDeadline(Todo todo, LocalDateTime createdAt) {
        if (todo.getDeadline().isBefore(createdAt)) {
            throw new DeadlineExceededException("마감일이 초과되어 Task를 생성할 수 없습니다.");
        }
    }

    public void update(String content) {
        validateForUpdate();

        if (content != null) {
            this.content = content;
        }
    }

    private void validateForUpdate() {
        if (this.status.isCompleted()) {
            throw new IllegalArgumentException("이미 완료된 Task는 수정할 수 없습니다.");
        }

        if (this.todo.getDeadline().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("마감일이 초과되어 수정할 수 없습니다..");
        }
    }

    public void complete() {
        checkDeadline(TaskStatus.COMPLETED);

        if (this.status.isCompleted()) {
            throw new IllegalStateException("이미 완료된 Task는 완료 처리할 수 없습니다.");
        }

        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void incomplete() {
        checkDeadline(TaskStatus.INCOMPLETE);

        if (!this.status.isCompleted()) {
            throw new IllegalStateException("완료되지 않은 Task는 해제할 수 없습니다.");
        }

        this.status = TaskStatus.INCOMPLETE;
        this.completedAt = null;
    }

    private void checkDeadline(TaskStatus status) {
        if (this.todo.getDeadline().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("마감일이 초과되어 " + status.getStatus() + " 처리할 수 없습니다.");
        }
    }
}
