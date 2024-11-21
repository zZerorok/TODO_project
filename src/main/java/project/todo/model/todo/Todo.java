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
}