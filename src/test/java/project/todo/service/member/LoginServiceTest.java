package project.todo.service.member;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import project.todo.exception.member.LoginFailedException;
import project.todo.model.member.Member;
import project.todo.repository.member.MemberRepository;
import project.todo.service.security.PasswordEncrypt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class LoginServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LoginService loginService;

    @Autowired
    private PasswordEncrypt passwordEncrypt;

    @BeforeEach
    void setUp() {
        var hashedPassword = passwordEncrypt.toHash("password123");
        var member = new Member(
                "사용자",
                "loginId",
                hashedPassword,
                "test@example.com"
        );
        memberRepository.save(member);
    }

    @DisplayName("아이디와 비밀번호가 일치하면 로그인 처리된다")
    @Test
    void login() {
        var loginRequest = new MemberLoginRequest(
                "loginId",
                "password123"
        );

        var login = loginService.login(loginRequest);

        assertThat(login).isNotNull();
        assertThat(login.name()).isEqualTo("사용자");
    }

    @DisplayName("아이디가 일치하지 않으면 예외 발생")
    @Test
    void loginWithWrongLoginId() {
        var loginRequest = new MemberLoginRequest(
                "wrongLoginId",
                "password123"
        );

        assertThatThrownBy(() -> loginService.login(loginRequest))
                .isInstanceOf(LoginFailedException.class)
                .hasMessage("로그인 정보가 일치하지 않습니다.");
    }

    @DisplayName("비밀번호가 일치하지 않으면 예외 발생")
    @Test
    void loginWithWrongPassword() {
        var loginRequest = new MemberLoginRequest(
                "loginId",
                "wrongPassword"
        );

        assertThatThrownBy(() -> loginService.login(loginRequest))
                .isInstanceOf(LoginFailedException.class)
                .hasMessage("로그인 정보가 일치하지 않습니다.");
    }

    @DisplayName("아이디와 비밀번호 모두 일치하지 않으면 예외 발생")
    @Test
    void loginWithWrongLoginIdAndPassword() {
        var loginRequest = new MemberLoginRequest(
                "wrongLoginId",
                "wrongPassword"
        );

        assertThatThrownBy(() -> loginService.login(loginRequest))
                .isInstanceOf(LoginFailedException.class)
                .hasMessage("로그인 정보가 일치하지 않습니다.");
    }
}
