package project.todo.model.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.todo.exception.member.LoginFailedException;
import project.todo.exception.member.NameLengthException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MemberTest {

    @DisplayName("주어진 값으로 새로운 Member 객체가 생성된다.")
    @Test
    void initMember() {

        assertDoesNotThrow(() -> {
            new Member(
                    "사용자",
                    "loginId",
                    "password123",
                    "test@example.com"
            );
        });
    }

    @DisplayName("Member 생성 시 이름이 10자를 초과하면 예외 발생")
    @Test
    void initMemberWithExceedingLength() {

        assertThatThrownBy(() -> {
            new Member(
                    "A".repeat(11),
                    "loginId",
                    "password123",
                    "test@example.com"
            );
        })
                .isInstanceOf(NameLengthException.class)
                .hasMessage("이름은 10자를 초과할 수 없습니다.");
    }

    @DisplayName("주어진 비밀번호와 회원의 비밀번호가 일치하지 않으면 예외를 발생")
    @Test
    void validatePassword() {
        var member = new Member(
                "사용자",
                "loginId",
                "password123",
                "test@example.com"
        );

        assertThatThrownBy(() -> member.validatePassword("password"))
                .isInstanceOf(LoginFailedException.class)
                .hasMessage("로그인 정보가 일치하지 않습니다.");
    }
}
