package project.todo.service.member;

import org.springframework.stereotype.Service;
import project.todo.model.member.Member;
import project.todo.repository.member.MemberRepository;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member save(MemberCreateRequest request) {
        Member member = new Member(request.name());
        memberRepository.save(member);
        return member;
    }
}
