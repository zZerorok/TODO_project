package project.todo.model;

import java.time.LocalDateTime;

public class Task {
    private long id;
    private String content;
    private LocalDateTime createdAt;
    private boolean isCompleted;
    private LocalDateTime completedAt;

    public Task(String content) {
        this.content = content;
        // 내용 작성한 날짜(시간 포함)
        this.createdAt = LocalDateTime.now();
        this.isCompleted = false;
    }
}
