package project.todo.model.todo;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.todo.exception.member.MemberException;
import project.todo.exception.todo.DeadlineExceededException;
import project.todo.exception.todo.DeadlineException;
import project.todo.exception.todo.TodoStateException;

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
    private Long writerId;
    private String title;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Todo(Long writerId, String title, LocalDate deadline) {
        this(writerId, title, deadline.atTime(LocalTime.MAX), LocalDateTime.now());
    }

    public Todo(Long writerId, String title, LocalDateTime deadline, LocalDateTime createdAt) {
        validateForCreate(title, deadline, createdAt);

        this.id = 0L;
        this.writerId = writerId;
        this.title = title;
        this.deadline = deadline;
        this.createdAt = createdAt;
        this.status = Status.INCOMPLETE;
    }

    private void validateForCreate(String title, LocalDateTime deadline, LocalDateTime createdAt) {
        validateTitle(title);
        validateDeadline(deadline, createdAt);
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 공백일 수 없습니다.");
        }
    }

    private void validateDeadline(LocalDateTime deadline, LocalDateTime createdAt) {
        if (deadline == null) {
            throw new DeadlineException("마감일을 설정해주세요.");
        }

        if (deadline.isBefore(createdAt)) {
            throw new DeadlineExceededException("마감일은 현재보다 과거일 수 없습니다.");
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
            throw new TodoStateException("이미 완료된 Todo는 수정할 수 없습니다.");
        }

        if (this.deadline.isBefore(LocalDateTime.now())) {
            throw new DeadlineExceededException("마감일이 초과되어 수정할 수 없습니다.");
        }
    }

    public void complete() {
        validateForUpdateStatus(Status.COMPLETE);

        if (this.status.isCompleted()) {
            throw new TodoStateException("이미 완료된 Todo는 완료 처리할 수 없습니다.");
        }

        this.status = Status.COMPLETE;
    }

    public void incomplete() {
        validateForUpdateStatus(Status.INCOMPLETE);

        if (!this.status.isCompleted()) {
            throw new TodoStateException("완료되지 않은 Todo는 해제할 수 없습니다.");
        }

        this.status = Status.INCOMPLETE;
    }

    public void validateForUpdateStatus(Status status) {
        if (this.deadline.isBefore(LocalDateTime.now())) {
            throw new DeadlineExceededException("마감일이 초과되어 " + status.getStatus() + " 처리할 수 없습니다.");
        }
    }

    public void validateWriter(long memberId) {
        if (!this.writerId.equals(memberId)) {
            throw new MemberException("작성자 정보가 일치하지 않습니다.");
        }
    }
}
