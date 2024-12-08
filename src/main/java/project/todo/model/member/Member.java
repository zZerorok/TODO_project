package project.todo.model.member;

import jakarta.persistence.*;

@Entity
public class Member {
    public static final int MAX_LENGTH = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    protected Member() {
    }

    public Member(String name) {
        validateName(name);

        this.name = name;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름을 입력해주세요.");
        }

        if (MAX_LENGTH < name.length()) {
            throw new IllegalArgumentException("이름은 10자를 초과할 수 없습니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
