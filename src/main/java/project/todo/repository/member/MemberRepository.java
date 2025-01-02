package project.todo.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import project.todo.model.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
