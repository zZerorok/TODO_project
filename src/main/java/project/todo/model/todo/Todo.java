package project.todo.model.todo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long memberId;
    private String title;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private TodoStatus status;

    public Todo(Long memberId, String title, LocalDate deadline) {
        this(memberId, title, deadline.atTime(LocalTime.MAX), LocalDateTime.now());
    }

    public Todo(Long memberId, String title, LocalDateTime deadline, LocalDateTime createdAt) {
        validateTitle(title);
        validateDeadline(deadline, createdAt);

        this.memberId = memberId;
        this.title = title;
        this.deadline = deadline;
        this.createdAt = createdAt;
        this.status = TodoStatus.INCOMPLETE;
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 공백일 수 없습니다.");
        }
    }

    private void validateDeadline(LocalDateTime deadline, LocalDateTime createdAt) {
        if (deadline == null) {
            throw new IllegalArgumentException("마감일을 설정해주세요.");
        }

        if (deadline.isBefore(createdAt)) {
            throw new IllegalArgumentException("마감일은 현재보다 과거일 수 없습니다.");
        }
    }

    public void update(String title, LocalDate deadline) {
        validateForUpdate();

        if (title != null) {
            this.title = title;
        }

        if (deadline != null) {
            this.deadline = deadline.atTime(LocalTime.MAX);
        }
    }

    private void validateForUpdate() {
        if (this.status.isCompleted()) {
            throw new IllegalArgumentException("이미 완료된 Todo는 수정할 수 없습니다.");
        }

        if (this.deadline.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("마감일이 초과되어 수정할 수 없습니다.");
        }
    }

    public void complete() {
        checkDeadline(TodoStatus.COMPLETED);

        if (this.status.isCompleted()) {
            throw new IllegalStateException("이미 완료된 Todo는 완료 처리할 수 없습니다.");
        }

        this.status = TodoStatus.COMPLETED;
    }

    public void incomplete() {
        checkDeadline(TodoStatus.INCOMPLETE);

        if (!this.status.isCompleted()) {
            throw new IllegalStateException("완료되지 않은 Todo는 해제할 수 없습니다.");
        }

        this.status = TodoStatus.INCOMPLETE;
    }

    private void checkDeadline(TodoStatus status) {
        if (this.deadline.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("마감일이 초과되어 " + status.getStatus() + " 처리할 수 없습니다.");
        }
    }
}
