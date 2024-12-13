package project.todo.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import project.todo.model.member.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
