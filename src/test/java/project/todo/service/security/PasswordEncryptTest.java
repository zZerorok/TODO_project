package project.todo.service.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordEncryptTest {
    private final PasswordEncrypt passwordEncrypt = new PasswordEncrypt();

    @DisplayName("비밀번호를 해싱하면 원본 비밀번호와 다른 값이 반환된다.")
    @Test
    void hash() {
        var password = "password123";

        var hashedPassword = passwordEncrypt.toHash(password);

        assertThat(hashedPassword).isNotEqualTo(password);
    }

    @DisplayName("해시 값의 길이는 64자이다.")
    @Test
    void size() {
        var password = "password 123";

        var hashedPassword = passwordEncrypt.toHash(password);

        assertThat(hashedPassword).hasSize(64);
    }

    @DisplayName("동일한 비밀번호는 항상 동일한 해시 값을 반환한다.")
    @Test
    void consistency() {
        var password = "password123!@";

        var hashedPassword1 = passwordEncrypt.toHash(password);
        var hashedPassword2 = passwordEncrypt.toHash(password);

        assertThat(hashedPassword1).isEqualTo(hashedPassword2);
    }

    @NullAndEmptySource
    @ParameterizedTest(name = "비밀번호 값이 공백이면 예외 발생")
    void invalidPassword(String password) {

        assertThatThrownBy(() -> passwordEncrypt.toHash(password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호를 입력해주세요.");
    }
}
