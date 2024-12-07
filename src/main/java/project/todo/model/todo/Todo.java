package project.todo.model.todo;

import jakarta.persistence.*;
import project.todo.model.member.Member;

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
    private LocalDateTime createdAt;
    private LocalDate deadline;
    private boolean isCompleted;
    private LocalDateTime completedAt;

    protected Todo() {
    }

    public Todo(Member member, String title, LocalDate deadline) {
        this(member, title, deadline, LocalDateTime.now());
    }

    public Todo(Member member, String title, LocalDate deadline, LocalDateTime createdAt) {
        this.member = member;
        this.title = title;
        this.createdAt = createdAt;
        this.deadline = deadline;
        this.isCompleted = false;
    }

    public void updateFromRequest(TodoUpdateRequest request) {
        if (this.isCompleted) {
            throw new IllegalStateException("이미 완료된 Todo는 수정할 수 없습니다.");
        }

        this.title = request.title();
        this.deadline = request.deadline();
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

    public String getTitle() {
        return title;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}
