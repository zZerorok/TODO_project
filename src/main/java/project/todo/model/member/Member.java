package project.todo.model.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member {
    public static final int MAX_LENGTH = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String loginId;
    private String password;
    private String email;
    private LocalDateTime createdAt;

    public Member(String name, String loginId, String password, String email) {
        this(name, loginId, password, email, LocalDateTime.now());
    }

    public Member(String name, String loginId, String password, String email, LocalDateTime createdAt) {
        validateLength(name);

        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
    }

    private void validateLength(String name) {
        if (MAX_LENGTH < name.length()) {
            throw new IllegalArgumentException("이름은 10자를 초과할 수 없습니다.");
        }
    }
}
