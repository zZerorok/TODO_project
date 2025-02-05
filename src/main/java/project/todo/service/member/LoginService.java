package project.todo.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.todo.exception.member.LoginFailedException;
import project.todo.model.member.Member;
import project.todo.repository.member.MemberRepository;
import project.todo.service.security.PasswordEncrypt;
import project.todo.service.security.dto.LoginMember;

/**
 * 사용자의 로그인 요청을 처리하는 서비스 클래스
 */
@RequiredArgsConstructor
@Service
public class LoginService {
    private final MemberRepository memberRepository;
    private final PasswordEncrypt passwordEncrypt;

    /**
     * 로그인 요청을 처리하여 로그인된 사용자 정보를 반환합니다.
     * @param request 로그인 요청 객체
     * @return {@link LoginMember} 로그인된 객체
     */
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
