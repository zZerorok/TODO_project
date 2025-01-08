package project.todo.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.todo.repository.member.MemberRepository;
import project.todo.service.security.LoginMember;
import project.todo.service.security.PasswordEncrypt;

@RequiredArgsConstructor
@Service
public class LoginService {
    private final MemberRepository memberRepository;
    private final PasswordEncrypt passwordEncrypt;

    public LoginMember login(MemberLoginRequest request) {
        var member = memberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new IllegalStateException("로그인 정보가 일치하지 않습니다."));

        var hashedPassword = passwordEncrypt.toHash(request.password());
        member.validatePassword(hashedPassword);

        return LoginMember.from(member);
    }
}
