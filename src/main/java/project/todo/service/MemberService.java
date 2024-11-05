package project.todo.service;

import org.springframework.stereotype.Service;
import project.todo.model.member.Member;
import project.todo.model.member.MemberRepository;

@Service
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void save(Member member) {
        memberRepository.save(member);
    }
}
