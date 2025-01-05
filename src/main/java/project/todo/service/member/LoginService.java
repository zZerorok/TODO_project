package project.todo.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.todo.model.member.LoginMember;
import project.todo.repository.member.MemberRepository;

@RequiredArgsConstructor
@Service
public class LoginService {
    private final MemberRepository memberRepository;

    public LoginMember login(MemberLoginRequest request) {
        var member = memberRepository.findByLoginId(request.loginId())
                .orElseThrow(() -> new IllegalStateException("로그인 정보가 일치하지 않습니다."));
        member.validatePassword(request.password());

        return LoginMember.from(member);
    }
}
