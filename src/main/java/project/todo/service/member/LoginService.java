package project.todo.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.todo.exception.member.LoginFailedException;
import project.todo.model.member.Member;
import project.todo.repository.member.MemberRepository;
import project.todo.service.security.LoginMember;
import project.todo.service.security.PasswordEncrypt;

@RequiredArgsConstructor
@Service
public class LoginService {
    private final MemberRepository memberRepository;
    private final PasswordEncrypt passwordEncrypt;

    public LoginMember login(MemberLoginRequest request) {
        var member = getMemberByLoginId(request.loginId());
        checkPasswordMatches(member, request.password());

        return LoginMember.from(member);
    }

    private Member getMemberByLoginId(String loginId) {
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new LoginFailedException("로그인 정보가 일치하지 않습니다."));
    }

    private void checkPasswordMatches(Member member, String password) {
        var hashedPassword = passwordEncrypt.toHash(password);
        member.validatePassword(hashedPassword);
    }
}
