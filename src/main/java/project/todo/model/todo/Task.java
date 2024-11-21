package project.todo.model.todo;

import jakarta.persistence.*;

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
        this.todo = todo;
        this.content = content;
        this.createdAt = createdAt;
        this.isCompleted = false;
    }
}
