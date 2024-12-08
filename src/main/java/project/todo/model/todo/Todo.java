package project.todo.model.todo;

import jakarta.persistence.*;
import project.todo.model.member.Member;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

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

    public void updateFrom(TodoUpdateRequest request) {
        if (this.isCompleted) {
            throw new IllegalStateException("이미 완료된 Todo는 수정할 수 없습니다.");
        }

        updateTitle(request.title());
        updateDeadline(request.deadline());
    }

    public void updateTitle(String title) {
        if (!this.title.equals(title)) {
            this.title = title;
        }
    }

    public void updateDeadline(LocalDate deadline) {
        if (!this.deadline.equals(deadline)) {
            this.deadline = deadline;
        }
    }

    public boolean isChanged(TodoUpdateRequest request) {
        return !this.title.equals(request.title())
                || !this.deadline.equals(request.deadline());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return isCompleted == todo.isCompleted && Objects.equals(id, todo.id) && Objects.equals(member, todo.member) && Objects.equals(title, todo.title) && Objects.equals(createdAt, todo.createdAt) && Objects.equals(deadline, todo.deadline) && Objects.equals(completedAt, todo.completedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, member, title, createdAt, deadline, isCompleted, completedAt);
    }
}
