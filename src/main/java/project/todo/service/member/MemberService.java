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
        if (memberRepository.existsByLoginId(request.loginId())) {
            throw new IllegalStateException("이미 존재하는 아이디 입니다.");
        }

        var hashedPassword = passwordEncrypt.toHash(request.password());
        var member = new Member(
                request.name(),
                request.loginId(),
                hashedPassword,
                request.email()
        );

        memberRepository.save(member);
    }
}
