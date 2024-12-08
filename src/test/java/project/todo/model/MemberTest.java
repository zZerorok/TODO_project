package project.todo.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import project.todo.model.member.Member;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @ValueSource(strings = {" ", "   "})
    @NullAndEmptySource
    @ParameterizedTest(name = "객체 생성 시 빈 문자열 또는 공백이면 예외 발생")
    void initMemberWithEmptyName(String name) {

        assertThatThrownBy(() -> new Member(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이름을 입력해주세요.");
    }


    @DisplayName("객체 생성 시 이름이 10자를 초과하면 예외 발생")
    @Test
    void initMemberWithExceedingLength() {

        assertThatThrownBy(() -> new Member("A".repeat(11)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이름은 10자를 초과할 수 없습니다.");
    }
}
