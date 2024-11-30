package project.todo.model.todo.task;

import jakarta.persistence.*;
import project.todo.exception.todo.DeadlineExceededException;
import project.todo.model.todo.Todo;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private boolean isCompleted;
    private LocalDateTime completedAt;

    protected Task() {
    }

    public Task(Todo todo, String content) {
        this(todo, content, LocalDateTime.now());
    }

    public Task(Todo todo, String content, LocalDateTime createdAt) {
        validateDeadline(todo, createdAt);

        this.todo = todo;
        this.content = content;
        this.createdAt = createdAt;
        this.isCompleted = false;
    }

    private void validateDeadline(Todo todo, LocalDateTime createdAt) {
        LocalDate deadline = todo.getDeadline();
        LocalDate createdDate = createdAt.toLocalDate();

        if (deadline.equals(createdDate) || createdDate.isAfter(deadline)) {
            throw new DeadlineExceededException(
                    String.format("마감일(%s)을 초과할 수 없습니다.", deadline));
        }
    }

    public void completeTask() {
        if (isCompleted()) {
            throw new IllegalStateException("이미 완료된 Task 입니다.");
        }

        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Todo getTodo() {
        return todo;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}
