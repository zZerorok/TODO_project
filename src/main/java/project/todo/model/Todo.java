package project.todo.model;

import java.time.LocalDateTime;

public class Todo {
    private long id;
    private Member member;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private boolean isCompleted;
    private LocalDateTime completedAt;

    public Todo(Member member, String title, LocalDateTime deadline) {
        this.member = member;
        this.title = title;
        this.createdAt = LocalDateTime.now();
        this.deadline = deadline;
        this.isCompleted = false;
    }
}
