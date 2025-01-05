package project.todo.service.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.todo.model.member.Member;
import project.todo.repository.member.MemberRepository;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public void register(MemberCreateRequest request) {
        if (memberRepository.existsByLoginId(request.loginId())) {
            throw new IllegalStateException("이미 존재하는 아이디 입니다.");
        }

        var member = new Member(
                request.name(),
                request.loginId(),
                request.password(),
                request.email()
        );

        memberRepository.save(member);
    }
}
