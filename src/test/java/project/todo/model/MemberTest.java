package project.todo.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import project.todo.model.member.Member;

class MemberTest {

    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @ParameterizedTest(name = "{0} 입력하면 예외를 던진다.")
    void invalidNameSpace(String invalidName) {
        Assertions.assertThatThrownBy(() -> new Member(invalidName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("사용자의 이름은 10글자를 넘어갈 수 없다.")
    void invalidNameLength() {
        Assertions.assertThatThrownBy(() -> new Member("A".repeat(11)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}