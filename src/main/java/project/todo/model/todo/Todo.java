package project.todo.model.todo;

import jakarta.persistence.*;
import project.todo.model.member.Member;
import project.todo.util.DateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
    private String title;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private boolean isCompleted;
    private LocalDateTime completedAt;

    protected Todo() {
    }

    public Todo(Member member, String title, LocalDate deadline) {
        this(member, title, DateUtils.toEndOfDay(deadline), LocalDateTime.now());
    }

    public Todo(Member member, String title, LocalDateTime deadline, LocalDateTime createdAt) {
        validateTitle(title);
        validateDeadline(deadline, createdAt);

        this.member = member;
        this.title = title;
        this.deadline = deadline;
        this.createdAt = createdAt;
        this.isCompleted = false;
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 공백일 수 없습니다.");
        }
    }

    private void validateDeadline(LocalDateTime deadline, LocalDateTime createdAt) {
        if (deadline.isBefore(createdAt)) {
            throw new IllegalArgumentException("마감일은 과거일 수 없습니다.");
        }
    }

    public void changeTitle(String title) {
        if (!this.title.equals(title)) {
            this.title = title;
        }
    }

    public void changeDeadline(LocalDateTime deadline) {
        if (!this.deadline.equals(deadline)) {
            this.deadline = deadline;
        }
    }

    public void complete() {
        if (this.isCompleted) {
            throw new IllegalStateException("이미 완료된 Todo 입니다.");
        }

        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getDeadline() {
        return deadline;
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
