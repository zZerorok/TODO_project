package project.todo.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.todo.model.member.Member;
import project.todo.repository.member.MemberRepository;
import project.todo.service.security.PasswordEncrypt;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncrypt passwordEncrypt;

    public void register(MemberCreateRequest request) {
        checkDuplicateLoginId(request.loginId());
        var member = new Member(
                request.name(),
                request.loginId(),
                encryptPassword(request.password()),
                request.email()
        );

        memberRepository.save(member);
    }

    private void checkDuplicateLoginId(String loginId) {
        if (memberRepository.existsByLoginId(loginId)) {
            throw new IllegalStateException("이미 존재하는 아이디 입니다.");
        }
    }

    private String encryptPassword(String rawPassword) {
        return passwordEncrypt.toHash(rawPassword);
    }
}
