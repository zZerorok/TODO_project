package project.todo.domain;

import java.time.LocalDateTime;

public class Task {
    private long id;
    private User user;
    private String content;
    private LocalDateTime createdAt;
    private boolean isCompleted;
    private LocalDateTime completedAt;

    public Task(User user, String content) {
        this.user = user;
        this.content = content;
        // 내용 작성한 날짜(시간 포함)
        this.createdAt = LocalDateTime.now();
        this.isCompleted = false;
    }
}
